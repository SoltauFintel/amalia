package github.soltaufintel.amalia.auth.rememberme;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public abstract class AbstractFileBasedKnownUserDAO implements IKnownUserDAO {

	@Override
	public IKnownUser get(String id) {
		return load().stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
	}
	
	@Override
	public void save(IKnownUser user) {
		// add
		List<SimpleKnownUser> list = load();
		list.add((SimpleKnownUser) user);
		save(list);
	}

	@Override
	public void delete(String userId) {
		List<SimpleKnownUser> newList = load().stream()
				.filter(i -> !i.getUserId().equals(userId))
				.collect(Collectors.toList());
		save(newList);
	}
	
	protected List<SimpleKnownUser> load() {
		List<SimpleKnownUser> list;
		if (file().isFile()) {
			try {
				String json = new String(Files.readAllBytes(file().toPath()));
				java.lang.reflect.Type type = new TypeToken<ArrayList<SimpleKnownUser>>() {}.getType();
				list = new Gson().fromJson(json, type);
			} catch (Exception ignore) {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
		}
		return list;
	}
	
	protected void save(List<SimpleKnownUser> list) {
		if (!file().getParentFile().isDirectory()) {
			file().getParentFile().mkdirs();
		}
		String json = new Gson().toJson(list);
		try (FileWriter w = new FileWriter(file())) {
			w.write(json);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected File file() {
		return new File(System.getProperty("user.home"), getFilename());
	}
	
	protected abstract String getFilename();
}
