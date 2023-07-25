package github.soltaufintel.amalia.web.templating;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

public class FormularGenerator extends AbstractFormularGenerator {

	public FormularGenerator(int indent) {
		super(indent);
	}

	public FormularGenerator withVersion() {
		return withVersion("version");
	}

	public FormularGenerator withVersion(String id) {
		version(id);
		return this;
	}
	
	public FormularGenerator textfield(String id, String label, int width) {
		return textfield(id, label, width, false, true);
	}
	
	public FormularGenerator textfield(String id, String label, int width, boolean autofocus, boolean withValue) {
		textfield(id, label, width, autofocus, withValue, newline);
		newline = false;
		return this;
	}

    public FormularGenerator checkbox(String id, String label, String label2, int width, boolean autofocus, boolean checked) {
        checkbox(id, label, label2, width, autofocus, checked, newline);
        newline = false;
        return this;
    }

    public FormularGenerator textarea(String id, String label, int width, int rows, boolean autofocus, boolean withValue) {
        textarea(id, label, width, rows, autofocus, withValue, newline);
        newline = false;
        return this;
    }

	public FormularGenerator combobox(String id, String label, int width, String items) {
		return combobox(id, label, width, items, false);
	}
	
	public FormularGenerator combobox(String id, String label, int width,
			String items, boolean autofocus) {
		combobox(id, label, width, items, autofocus, newline);
		newline = false;
		return this;
	}

	public FormularGenerator newline() {
		endline();
		newline = true;
		return this;
	}
	
	public FormularGenerator withoutButtons() {
		buttons = false;
		for (DataMap model : fields) {
			model.put("disabled", true);
		}
		return this;
	}
	
	public FormularGenerator save(String save) {
	    this.save = save;
	    return this;
	}
    
    public FormularGenerator cancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public FormularGenerator submit(String submit1, String submit2) {
        this.submit1 = submit1;
        this.submit2 = submit2;
        return this;
    }

	@Override
	public String getHTML(String action, String hrefCancel) {
		DataMap model = createModel(action, hrefCancel);
		DataList list = model.list("fields");
		endline();
		for (DataMap m : fields) {
			list.add().put("html", templates.render(m.get("template").toString(), m));
		}
		return templates.render("formular", model);
	}
	
	private void endline() {
		if (!fields.isEmpty()) {
			fields.get(fields.size() - 1).put("endline", true);
		}
	}
}
