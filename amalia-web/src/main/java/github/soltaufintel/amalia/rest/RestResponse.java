package github.soltaufintel.amalia.rest;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

// TODO REST umbauen. Wenn mich die response gar nicht interessiert, muss ich close() aufrufen. Das ist doof.
public class RestResponse {
	private final CloseableHttpResponse response;
	private CloseableHttpClient httpClient;
	
	RestResponse(CloseableHttpResponse response, CloseableHttpClient httpClient) {
		this.response = response;
		this.httpClient = httpClient;
	}
	
	/**
	 * You must call close() after data is fetched from response.
	 * @return response
	 */
	public HttpResponse getHttpResponse() {
		return response;
	}

	/**
	 * Must only be called if getHttpResponse() has been called.
	 */
	public void close() {
		if (httpClient != null) {
			try {
				httpClient.close();
				httpClient = null;
			} catch (Exception quietly) {
			}
		}
	}

	public String response() {
		try {
			String ret = EntityUtils.toString(response.getEntity()); // must fetch result before closing http client
			close();
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public <T> T fromJson(Class<T> cls) {
		return new Gson().fromJson(response(), cls);
	}

	/**
	 * @param <T> any type
	 * @param type example: <code>java.lang.reflect.Type type = new TypeToken&lt;ArrayList&lt;TheItemClass&gt;&gt;() {}.getType();</code>
	 * @return object
	 */
	public <T> T fromJson(Type type) {
		return new Gson().fromJson(response(), type);
	}
}
