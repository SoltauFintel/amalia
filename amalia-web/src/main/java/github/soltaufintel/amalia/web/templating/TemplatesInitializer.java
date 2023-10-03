package github.soltaufintel.amalia.web.templating;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.compiler.TemplateCompiler;
import com.github.template72.compiler.TemplateCompilerContext;
import com.github.template72.compiler.preprocessor.CommentPreprocessor;
import com.github.template72.compiler.preprocessor.IncludePreprocessor;
import com.github.template72.compiler.preprocessor.MasterPreprocessor;
import com.github.template72.compiler.preprocessor.TemplatePreprocessor;
import com.github.template72.loader.ResourceTemplateLoader;
import com.github.template72.syntax.TemplateSyntax;

import github.soltaufintel.amalia.web.action.Page;
import github.soltaufintel.amalia.web.builder.Initializer;
import github.soltaufintel.amalia.web.config.AppConfig;

public class TemplatesInitializer implements Initializer {
	public static Replacer fp;
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
		ResourceTemplateLoader loader = new ResourceTemplateLoader() {
	        @Override
            public String charsetName() {
                return "UTF-8";
            }
        };
		
		TemplateCompilerContext ctx = new TemplateCompilerContext(TemplateSyntax.DEFAULT, loader);
		fp = new Replacer(ctx, null);
		TemplatePreprocessor preprocessor = new CommentPreprocessor(ctx,
					new MasterPreprocessor(ctx,
					new IncludePreprocessor(ctx, fp)));
		ctx.setPreprocessor(preprocessor);
		TemplateCompiler compiler = new TemplateCompiler(ctx);
		
		Page.templates = new CompiledTemplates(compiler, new AmaliaTemplateFileCache(), config.isDevelopment(), pages);
	}
}
