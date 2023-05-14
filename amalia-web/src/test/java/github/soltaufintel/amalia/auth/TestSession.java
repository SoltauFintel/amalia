package github.soltaufintel.amalia.auth;

import java.util.HashMap;
import java.util.Map;

import github.soltaufintel.amalia.auth.webcontext.Session;

public class TestSession extends Session {
	public final Map<String, String> session = new HashMap<>();

	public TestSession() {
		super(null);
	}

	@Override
	protected String get(String name) {
		return session.get(name);
	}
	
	@Override
	protected void set(String name, String value) {
		session.put(name, value);
	}
}
