package github.soltaufintel.amalia.web.action;

import java.util.Collection;
import java.util.List;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

public abstract class Page extends Action {
    public static CompiledTemplates templates;
    protected final DataMap model = new DataMap();

    @Override
    protected String render() {
    	if (isPOST()) {
    		return "";
    	} else {
    		return templates.render(getPage(), model);
    	}
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

    public DataList list(String name) {
        return model.list(name);
    }

    public void combobox(String listName, List<String> items, String selected, boolean withEmptyItem, DataMap model) {
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
    
    public boolean isPOST() {
    	return ctx.isPOST();
    }
}
