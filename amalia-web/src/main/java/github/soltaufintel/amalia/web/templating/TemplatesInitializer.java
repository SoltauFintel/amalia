package github.soltaufintel.amalia.web.templating;

import org.pmw.tinylog.Logger;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.compiler.TemplateCompiler;
import com.github.template72.compiler.TemplateCompilerBuilder;
import com.github.template72.loader.TemplateFileCache;

import github.soltaufintel.amalia.web.action.Page;
import github.soltaufintel.amalia.web.builder.Initializer;
import github.soltaufintel.amalia.web.config.AppConfig;

public class TemplatesInitializer implements Initializer {
	private final String[] pages;
	
	/**
	 * @param pages short file names of all directly used templates,
	 *              but without templates which use the formula generator
	 */
	public TemplatesInitializer(String ...pages) {
		this.pages = pages;
	}
	
	@Override
	public void init(AppConfig config) {
		_init(config, pages);
	}
	
	public static void _init(AppConfig config, String[] pages) {
	    for (String template : pages) {
	        Logger.debug("template: " + template);
	    }
        TemplateCompiler compiler = new TemplateCompilerBuilder().withUTF8Loader().build();
		Page.templates = new CompiledTemplates(compiler, new TemplateFileCache(), config.isDevelopment(), pages);
	}
}
