package github.soltaufintel.amalia.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.github.template72.data.DataMap;

/**
 * National language support
 */
public class NLSService {
    
    public static Map<String, String> loadRB(String language, String filename, Class<?> cls) {
        // Properties Klasse ist doof
        Map<String, String> map = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(filename)))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().startsWith("#")) {
                    continue;
                }
                int o = line.indexOf("=");
                if (o >= 0) {
                    String key = line.substring(0, o).trim();
                    String value = line.substring(o + 1).trim();
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading NLS file '" + filename + "' for language '" + language + "'", e);
        }
        return map;
    }

    public static DataMap loadDataMap(Map<String, String> map) {
        DataMap dataMap = new DataMap();
        dataMap.putAll(map);
        return dataMap;
    }
}
