package github.soltaufintel.amalia.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonFile<T> {
    private List<T> list;

    public JsonFile() {
    }

    /**
     * load file constructor
     * @param file -
     */
    public JsonFile(File file) {
        @SuppressWarnings("unchecked")
        JsonFile<T> loadedFile = FileService.loadJsonFile(file, JsonFile.class);
        list = loadedFile == null || loadedFile.list == null ? new ArrayList<>() : loadedFile.list;
    }

    public JsonFile(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void save(File file) {
        FileService.saveJsonFile(file, this);
    }
}
