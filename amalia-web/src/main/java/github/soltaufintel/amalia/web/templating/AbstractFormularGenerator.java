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
			"combobox", "formular", "textfield", "version");
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
	}
	
	protected void textfield(String id, String label, int width, boolean autofocus, boolean withValue, boolean newline) {
		DataMap model = new DataMap();
		standard("textfield", id, label, width, autofocus, model);
		model.put("newline", newline);
		model.put("withValue", withValue);
		fields.add(model);
	}

	protected void combobox(String id, String label, int width,
			String items, boolean autofocus, boolean newline) {
		DataMap model = new DataMap();
		standard("combobox", id, label, width, autofocus, model);
		model.put("newline", newline);
		model.put("items", items);
		fields.add(model);
	}

	public abstract String getHTML(String action, String hrefCancel);
	
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
