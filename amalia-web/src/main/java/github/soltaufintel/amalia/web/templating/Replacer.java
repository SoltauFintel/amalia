package github.soltaufintel.amalia.web.templating;

import com.github.template72.compiler.TemplateCompilerContext;
import com.github.template72.compiler.preprocessor.TemplatePreprocessor;

public class Replacer implements TemplatePreprocessor {
	private final TemplatePreprocessor next;
	private String varName = "{{formular}}";
	private String content = "";
	
	public Replacer(TemplateCompilerContext ctx, TemplatePreprocessor next) {
		this.next = next;
	}

	@Override
	public String render(String template) {
		if (next != null) {
			template = next.render(template);
		}
		String n = getVarName();
		if (n != null && !n.isBlank()) {
			template = template.replace(n, getContent());
		}
		return template;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? "" : content;
	}
}
