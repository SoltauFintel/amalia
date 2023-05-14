package github.soltaufintel.amalia.rest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class RestResponse {
	private final CloseableHttpResponse response;
	
	RestResponse(CloseableHttpResponse response) {
		this.response = response;
	}
	
	public HttpResponse getHttpResponse() {
		return response;
	}
	
	public String response() {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public <T> T fromJson(Class<T> cls) {
		return new Gson().fromJson(response(), cls);
	}
}
