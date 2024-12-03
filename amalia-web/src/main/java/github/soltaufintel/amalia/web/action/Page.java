package github.soltaufintel.amalia.web.action;

import java.util.Collection;
import java.util.List;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

public abstract class Page extends Action {
    public static CompiledTemplates templates;
    protected final DataMap model = new DataMap();
    protected boolean render = true;

    @Override
    protected String render() {
        if (!render || isPOST()) {
            return "";
        } else {
            return templates.render(getPage(), model);
        }
    }

    public boolean isPOST() {
        return ctx.isPOST();
    }

    protected String getPage() {
        return this.getClass().getSimpleName();
    }
    
    public void put(String name, String text) {
        model.put(name, text);
    }

    public void put(String name, boolean condition) {
        model.put(name, condition);
    }
    
    public void putInt(String name, int number) {
        model.putInt(name, number);
    }
    
    public void putHas(String name, Object o) {
        model.putHas(name, o);
    }

    public void put_putHas(String name, String text) {
        model.put(name, text);
        model.putHas(name, text);
    }

    public void putSize(String name, Collection<?> collection) {
        model.putSize(name, collection);
    }

    public void put(String name, Page component) {
        component.init(ctx);
        String html = component.run();
        put(name, html); // no esc
    }

    public void put(String name, Action component) {
        component.init(ctx);
        String html = component.run();
        put(name, html); // no esc
    }

    public DataList list(String name) {
        return model.list(name);
    }

    /**
     * single-select combobox
     * @param listName -
     * @param items -
     * @param selected can be null or empty
     * @param withEmptyItem -
     */
    public void combobox(String listName, List<String> items, String selected, boolean withEmptyItem) {
        ComboboxCreator.combobox(listName, items, selected, withEmptyItem, model);
    }

    /**
     * single-select combobox
     * @param listName -
     * @param items ID+Label list
     * @param selected ID can be null or empty
     * @param withEmptyItem -
     */
    public void combobox_idAndLabel(String listName, List<IdAndLabel> items, String selected, boolean withEmptyItem) {
        ComboboxCreator.combobox_idAndLabel(listName, items, selected, withEmptyItem, model);
    }

    /**
     * multi-select combobox
     * @param listName -
     * @param items -
     * @param selectedItems list can be null
     * @param withEmptyItem -
     */
    public void combobox(String listName, List<String> items, List<String> selectedItems, boolean withEmptyItem) {
        ComboboxCreator.combobox(listName, items, selectedItems, withEmptyItem, model);
    }

    /**
     * multi-select combobox
     * @param listName -
     * @param items ID+Label list
     * @param selectedItems ID list, list can be null
     * @param withEmptyItem -
     */
    public void combobox_idAndLabel(String listName, List<IdAndLabel> items, List<String> selectedItems, boolean withEmptyItem) {
        ComboboxCreator.combobox_idAndLabel(listName, items, selectedItems, withEmptyItem, model);
    }
}
