package github.soltaufintel.amalia.base;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class JsonFileDAO<T> {

    public List<T> load(File file) {
        return new JsonFile<T>(file).getList();
    }

    public List<T> loadAll(File folder, Collection<String> names) {
        List<T> ret = new ArrayList<>();
        for (String name : names) {
            ret.addAll(load(new File(folder, name + ".json")));
        }
        return ret;
    }

    public void save(List<T> list, File file) {
        new JsonFile<T>(list).save(file);
    }
}