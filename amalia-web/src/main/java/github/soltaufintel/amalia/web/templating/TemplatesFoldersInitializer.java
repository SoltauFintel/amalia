package github.soltaufintel.amalia.web.templating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import github.soltaufintel.amalia.web.builder.Initializer;
import github.soltaufintel.amalia.web.config.AppConfig;

public class TemplatesFoldersInitializer implements Initializer {
	private final Class<?> ref;
	private final String[] folders;
	private String extension = ".html";
	
	public TemplatesFoldersInitializer(Class<?> ref, String... folders) {
		this.ref = ref;
		this.folders = folders;
	}
	
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public void init(AppConfig config) {
		List<String> list = new ArrayList<>();
		for (String folder : folders) {
			list.addAll(getResourceFiles(folder));
		}
		TemplatesInitializer._init(config, list.toArray(new String[0]));
	}
	
	private List<String> getResourceFiles(String path) {
		List<String> filenames = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getResourceAsStream(path)))) {
			String resource;
			while ((resource = br.readLine()) != null) {
				if (resource.endsWith(extension)) {
					filenames.add(resource);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error finding template files for folder " + path, e);
		}
		return filenames;
	}

	private InputStream getResourceAsStream(String resource) {
		final InputStream in = getContextClassLoader().getResourceAsStream(resource);
		return in == null ? ref.getResourceAsStream(resource) : in;
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
