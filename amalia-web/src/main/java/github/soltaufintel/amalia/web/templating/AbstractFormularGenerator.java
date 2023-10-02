package github.soltaufintel.amalia.web.templating;

import java.util.ArrayList;
import java.util.List;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.compiler.TemplateCompiler;
import com.github.template72.compiler.TemplateCompilerBuilder;
import com.github.template72.data.DataMap;
import com.github.template72.loader.ResourceTemplateLoader;
import com.github.template72.loader.TemplateFileCache;

public abstract class AbstractFormularGenerator {
    public static String STARTSEP = "{{";
    public static String ENDSEP = "}}";
	protected static CompiledTemplates templates;
	protected List<DataMap> fields = new ArrayList<>();
	protected final String indent;
	protected String version = "";
	protected boolean newline = false;
	protected boolean buttons = true;
	protected String save = "Speichern";
	protected String cancel = "Abbruch";
	protected String submit1 = "";
	protected String submit2 = "";
	
	static {
		TemplateCompiler compiler = new TemplateCompilerBuilder()
			.withSyntax("::", ";;")
			.withLoader(new ResourceTemplateLoader("/formular/", ".html") {
		        @Override
	            public String charsetName() {
	                return "UTF-8";
	            }
	        })
			.build();
		templates = new CompiledTemplates(compiler, new TemplateFileCache(), false,
			"checkbox", "combobox", "formular", "textarea", "textfield", "version");
	}
	
	public AbstractFormularGenerator(int indent) {
		this.indent = "" + indent;
	}
	
	protected void version(String id) {
		DataMap model = new DataMap();
		model.put("id", id);
		version = templates.render("version", model);
	}

	protected void standard(String template, String id, String label, int width, boolean autofocus, DataMap model) {
		model.put("template", template);
		model.put("id", id);
		model.put("indent", indent);
		model.put("width", "" + width);
		model.put("label", label);
		model.put("autofocus", autofocus ? " autofocus" : "");
		model.put("disabled", false);
		model.put("endline", false);
		model.put("spacer", "");
	}
	
    protected void textfield(String id, String label, int width, boolean autofocus, boolean withValue, boolean newline) {
        DataMap model = new DataMap();
        standard("textfield", id, label, width, autofocus, model);
        model.put("newline", newline);
        model.put("withValue", withValue);
        fields.add(model);
    }

    protected void checkbox(String id, String label, int width, boolean autofocus, boolean checked, boolean newline) {
        DataMap model = new DataMap();
        standard("checkbox", id, label, width, autofocus, model);
        model.put("newline", newline);
        model.put("checked", checked);
        fields.add(model);
    }

    protected void textarea(String id, String label, int width, int rows, boolean autofocus, boolean withValue, boolean newline) {
        DataMap model = new DataMap();
        standard("textarea", id, label, width, autofocus, model);
        model.put("newline", newline);
        model.put("withValue", withValue);
        model.putInt("rows", rows);
        fields.add(model);
    }

	protected void combobox(String id, String label, int width, String items, boolean autofocus, boolean newline) {
		DataMap model = new DataMap();
		standard("combobox", id, label, width, autofocus, model);
		model.put("newline", newline);
		model.put("items", items);
        model.put("listbox", false);
        model.putInt("size", 0);
        model.put("idAndLabel", false);
		fields.add(model);
	}

    protected void listbox(String id, String label, int width, String items, boolean autofocus, boolean newline, int size, boolean idAndLabel) {
        DataMap model = new DataMap();
        standard("combobox", id, label, width, autofocus, model);
        model.put("newline", newline);
        model.put("items", items);
        model.put("listbox", size > 0);
        model.putInt("size", size);
        model.put("idAndLabel", idAndLabel);
        fields.add(model);
    }
    
    protected void _empty(int width) {
        DataMap model = new DataMap();
        model.put("template", "empty");
        model.put("indent", "" + (indent  ));
        model.put("width", "" + (+ width));
        model.put("spacer", "");
        fields.add(model);
    }
    
    protected void _spacer(int width) {
        fields.get(fields.size() - 1).put("spacer", "<div class=\"col-lg-" + width + "\"></div>");
    }

	public abstract String getHTML(String action, String hrefCancel);

	/**
	 * Use this method if action link and/or cancel link are variable values.
	 * @param model -
	 * @param action path
	 * @param hrefCancel path
	 * @return HTML
	 */
    public String getHTML(DataMap model, String actionlink, String cancellink) {
        String a = "formularActionLink";
        String c = "formularCancelLink";
        model.put(a, actionlink);
        model.put(c, cancellink);
        return getHTML(STARTSEP + a + ENDSEP, STARTSEP + c + ENDSEP);
    }

	protected DataMap createModel(String action, String hrefCancel) {
		DataMap model = new DataMap();
		model.put("indent", indent);
		model.put("version", version);
		model.put("hasButtons", buttons);
		model.put("action", action);
		model.put("hrefCancel", hrefCancel);
		model.put("save", save);
		model.put("cancel", cancel);
		model.put("submit1", submit1);
		model.put("submit2", submit2);
		return model;
	}
}
