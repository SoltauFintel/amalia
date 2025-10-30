package github.soltaufintel.amalia.web.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Gives access to the application configuration
 * 
 * <p>
 * Takes config filename from CONFIG env var. If it's not set
 * "AppConfig.properties" is used. If "AppConfig.properties" does not exist
 * "/AppConfig.properties" will be used.
 * </p>
 */
public class AppConfig {
    private final Properties properties;
    private final String configFile;

    public AppConfig() {
        String dn = System.getenv("CONFIG");
        if (dn == null || dn.isBlank()) {
            dn = "AppConfig.properties";
        }
        properties = new Properties();
        configFile = load(dn);
    }
    
    public AppConfig(String dn) {
        properties = new Properties();
        configFile = load(dn);
    }
    
    /** for test */
    public AppConfig(Properties properties, String configFile) {
        this.properties = properties;
        this.configFile = configFile;
    }
    
    /**
     * @param dn name of config file
     * @return name of used config file
     */
    protected String load(String dn) {
        try {
            properties.load(new FileReader(dn));
        } catch (IOException e1) {
            if (dn.startsWith("/")) {
                throw new RuntimeException(e1);
            }
            try {
                dn = "/" + dn;
                properties.load(new FileReader(dn));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dn;
    }

    /**
     * @param key
     * @return null if property does not exist
     */
    public String get(String key) {
        return get(key, null);
    }

    /**
     * @param key -
     * @param pDefault -
     * @return pDefault if property does not exist.
     * If property value starts with "!!" the following value is used as environment variable and its value is returned.
     */
    public String get(String key, String pDefault) {
        String ret = properties.getProperty(key);
        if (ret != null && ret.startsWith("!!")) {
            ret = System.getenv(ret.substring("!!".length()));
        }
        return ret == null ? pDefault : ret;
    }
    
    public int getInt(String key, int pDefault) {
        String a = get(key, null);
        return a == null ? pDefault : Integer.parseInt(a);
    }

    public String getFilename() {
        return configFile;
    }

    public boolean isDevelopment() {
        return "true".equals(get("development"));
    }

    /**
     * @param key
     * @return true if the key exists and has got a non-empty value
     */
    public boolean hasFilledKey(String key) {
        String value = get(key);
        return value != null && !value.isBlank();
    }
    
    /**
     * @return port number from configuration file
     * (or if not set default from env var 'PORT'
     *  or if not set built-in value 8080)
     */
    public int getPort() {
        int de = 8080;
        String p = System.getenv("PORT");
        if (p != null && !p.isBlank()) {
            de = Integer.parseInt(p);
        }
        return getInt("port", de);
    }

    public List<Integer> getPorts() {
    	List<Integer> ports = splitPorts(System.getenv("PORTS"));
    	if (ports == null) {
    		ports = splitPorts(get("ports"));
    	}
    	return ports;
    }
    
    private List<Integer> splitPorts(String ports) {
        if (ports != null && !ports.isBlank()) {
        	List<Integer> ret = new ArrayList<>();
        	for (String i : ports.split(",")) {
        		ret.add(Integer.parseInt(i.trim()));
        	}
        	return ret;
        }
        return null;
    	
    }
}
