package github.soltaufintel.amalia.web.action;

import java.util.List;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

public class ComboboxCreator {

    private ComboboxCreator() {
    }
    
    /**
     * single-select combobox
     * @param listName -
     * @param items -
     * @param selected can be null or empty
     * @param withEmptyItem -
     * @param model -
     */
    public static void combobox(String listName, List<String> items, String selected, boolean withEmptyItem, DataMap model) {
        DataList list = model.list(listName);
        if (withEmptyItem) {
            DataMap map1 = list.add();
            map1.put("text", "");
            map1.put("selected", selected == null || selected.isBlank());
        }
        items.forEach(text -> {
            DataMap map = list.add();
            map.put("text", Escaper.esc(text));
            map.put("selected", text.equals(selected));
        });
    }

    /**
     * single-select combobox
     * @param listName -
     * @param items ID+Label list
     * @param selected ID, can be null or empty
     * @param withEmptyItem -
     * @param model -
     */
    public static void combobox_idAndLabel(String listName, List<IdAndLabel> items, String selected, boolean withEmptyItem, DataMap model) {
        DataList list = model.list(listName);
        if (withEmptyItem) {
            DataMap map1 = list.add();
            map1.put("id", "");
            map1.put("text", "");
            map1.put("selected", selected == null || selected.isBlank());
        }
        items.forEach(item -> {
            DataMap map = list.add();
            map.put("id", Escaper.esc(item.getId()));
            map.put("text", Escaper.esc(item.getLabel()));
            map.put("selected", item.getId().equals(selected));
        });
    }

    /**
     * multi-select combobox
     * @param listName -
     * @param items -
     * @param selectedItems list can be null
     * @param withEmptyItem -
     * @param model -
     */
    public static void combobox(String listName, List<String> items, List<String> selectedItems, boolean withEmptyItem, DataMap model) {
        DataList list = model.list(listName);
        if (withEmptyItem) {
            DataMap map1 = list.add();
            map1.put("text", "");
            map1.put("selected", selectedItems == null || selectedItems.isEmpty() || selectedItems.contains(""));
        }
        items.forEach(text -> {
            DataMap map = list.add();
            map.put("text", Escaper.esc(text));
            map.put("selected", selectedItems != null && selectedItems.contains(text));
        });
    }

    /**
     * multi-select combobox
     * @param listName -
     * @param items ID+Label list
     * @param selectedItems ID list, list can be null
     * @param withEmptyItem -
     * @param model -
     */
    public static void combobox_idAndLabel(String listName, List<IdAndLabel> items, List<String> selectedItems, boolean withEmptyItem, DataMap model) {
        DataList list = model.list(listName);
        if (withEmptyItem) {
            DataMap map1 = list.add();
            map1.put("id", "");
            map1.put("text", "");
            map1.put("selected", selectedItems == null || selectedItems.isEmpty() || selectedItems.contains(""));
        }
        items.forEach(item -> {
            DataMap map = list.add();
            map.put("id", Escaper.esc(item.getId()));
            map.put("text", Escaper.esc(item.getLabel()));
            map.put("selected", selectedItems != null && selectedItems.contains(item.getId()));
        });
    }
}
