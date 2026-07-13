package github.soltaufintel.amalia.fg;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.compiler.TemplateCompilerBuilder;
import com.github.template72.data.DataMap;
import com.github.template72.loader.ResourceTemplateLoader;
import com.github.template72.loader.TemplateFileCache;

public class FormularComponentTemplates {
    private final CompiledTemplates templates;
    private int indent = 2;
    
    public FormularComponentTemplates(boolean development) {
        var loader = new ResourceTemplateLoader("/form/", ".html") {
            @Override
            public String charsetName() {
                return "UTF-8";
            }
        };
        var compiler = new TemplateCompilerBuilder().withLoader(loader).build();
        templates = new CompiledTemplates(compiler, new TemplateFileCache(), development);
    }
    
    public String render(String templateFile, DataMap model) {
        return templates.render(templateFile, model);
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }
}
