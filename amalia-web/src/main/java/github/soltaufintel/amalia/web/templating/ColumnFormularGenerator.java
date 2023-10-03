package github.soltaufintel.amalia.web.templating;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

public class ColumnFormularGenerator extends AbstractFormularGenerator {
	private final int columns;
	
	public ColumnFormularGenerator(int indent, int columns) {
		super(indent);
		this.columns = columns;
	}

	public ColumnFormularGenerator withVersion() {
		return withVersion("version");
	}

	public ColumnFormularGenerator withVersion(String id) {
		version(id);
		return this;
	}
	
	public ColumnFormularGenerator textfield(String id, String label, int width) {
		return textfield(id, label, width, false, true);
	}
	
	public ColumnFormularGenerator textfield(String id, String label, int width, boolean autofocus, boolean withValue) {
		textfield(id, label, width, autofocus, withValue, false);
		return this;
	}

    public ColumnFormularGenerator checkbox(String id, String label, int width, boolean autofocus) {
        checkbox(id, label, width, autofocus, false);
        return this;
    }

    public ColumnFormularGenerator textarea(String id, String label, int width, int rows, boolean autofocus, boolean withValue) {
        textarea(id, label, width, rows, autofocus, withValue, false);
        return this;
    }

	public ColumnFormularGenerator combobox(String id, String label, int width, String items) {
		return combobox(id, label, width, items, false);
	}
	
	public ColumnFormularGenerator combobox(String id, String label, int width, String items, boolean autofocus) {
		combobox(id, label, width, items, autofocus, false);
		return this;
	}

    public ColumnFormularGenerator combobox_idAndLabel(String id, String label, int width, String items, boolean autofocus) {
        return listbox_idAndLabel(id, label, width, items, autofocus, 0, false);
    }

    public ColumnFormularGenerator listbox(String id, String label, int width, String items, boolean autofocus, int size, boolean multiple) {
        listbox(id, label, width, items, autofocus, false, size, false, multiple);
        return this;
    }

    public ColumnFormularGenerator listbox_idAndLabel(String id, String label, int width, String items, boolean autofocus, int size, boolean multiple) {
        listbox(id, label, width, items, autofocus, false, size, true, multiple);
        return this;
    }

    public ColumnFormularGenerator empty(int width) {
        _empty(width);
        return this;
    }

    public ColumnFormularGenerator spacer(int width) {
        _spacer(width);
        return this;
    }
    
    public ColumnFormularGenerator newline() {
        fields.get(fields.size() - 1).put("forceNewline", "1");
        return this;
    }

	public ColumnFormularGenerator withoutButtons() {
		buttons = false;
		for (DataMap model : fields) {
			model.put("disabled", true);
		}
		return this;
	}
    
    public ColumnFormularGenerator save(String save) {
        this.save = save;
        return this;
    }
    
    public ColumnFormularGenerator cancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public ColumnFormularGenerator submit(String submit1, String submit2) {
        this.submit1 = submit1;
        this.submit2 = submit2;
        return this;
    }

	@Override
	public String getHTML(String action, String hrefCancel) {
		DataMap model = createModel(action, hrefCancel);
		DataList list = model.list("fields");
		int last = fields.size() - 1;
		int lastInGroup = columns - 1;
		for (int i = 0; i <= last; i++) {
			DataMap m = fields.get(i);
			
			m.put("newline", i % columns == 0);
			m.put("endline", i % columns == lastInGroup || i == last);
			list.add().put("html", templates.render(m.get("template").toString(), m));
		}
		return templates_noCache.render("formular", model);
	}
}
