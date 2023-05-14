package github.soltaufintel.amalia.rest;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * REST call via http
 */
public class REST {
	private final URI uri;
	private String authorization = null;
	private boolean handleErrors = true;
	private CloseableHttpClient client = null;
	
	public REST(String url) {
		this(URI.create(url));
	}

	public REST(URI uri) {
		this.uri = uri;
	}
	
	public REST withAuthorization(String authorization) {
		REST rest = new REST(uri);
		rest.authorization = authorization;
		rest.handleErrors = handleErrors;
		rest.client = client;
		return rest;
	}
	
	public REST withoutErrorHandling() {
		REST rest = new REST(uri);
		rest.authorization = authorization;
		rest.handleErrors = false;
		rest.client = client;
		return rest;
	}
	
	public REST withClient(CloseableHttpClient client) {
		REST rest = new REST(uri);
		rest.authorization = authorization;
		rest.handleErrors = handleErrors;
		rest.client = client;
		return rest;
	}

	public RestResponse get() {
		return doRequest(new HttpGet(uri));
	}
	
	public RestResponse post(String str) {
		return request(new HttpPost(uri), str);
	}
	public RestResponse post(String str, String contentType) {
		return request(new HttpPost(uri), str, contentType);
	}
	public RestResponse post(Object object) {
		return request(new HttpPost(uri), object);
	}
	
	public RestResponse put(String str) {
		return request(new HttpPut(uri), str);
	}
	public RestResponse put(String str, String contentType) {
		return request(new HttpPut(uri), str, contentType);
	}
	public RestResponse put(Object object) {
		return request(new HttpPut(uri), object);
	}
	
	public RestResponse patch(String str) {
		return request(new HttpPatch(uri), str);
	}
	public RestResponse patch(String str, String contentType) {
		return request(new HttpPatch(uri), str, contentType);
	}
	public RestResponse patch(Object object) {
		return request(new HttpPatch(uri), object);
	}
	
	public RestResponse delete() {
		return doRequest(new HttpDelete(uri));
	}
	
	protected RestResponse request(HttpEntityEnclosingRequestBase request, String str) {
		return request(request, str, null);
	}

	protected RestResponse request(HttpEntityEnclosingRequestBase request, String str, String contentType) {
		try {
			StringEntity entity = new StringEntity(str);
			entity.setContentType(contentType);
			request.setEntity(entity);
			return doRequest(request);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected RestResponse request(HttpEntityEnclosingRequestBase request, Object object) {
		return request(request, new Gson().toJson(object), "application/json");
	}
	
	protected RestResponse doRequest(HttpRequestBase request) {
		initRequest(request);
		CloseableHttpClient httpClient = client == null ? createClient() : client;
		CloseableHttpResponse response;
		try {
			response = httpClient.execute(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()),
					request, HttpClientContext.create());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (handleErrors) {
			handleError(response);
		}
		if (client == null) {
			closeClient(httpClient);
		}
		return new RestResponse(response);
	}
	
	protected void initRequest(HttpRequestBase request) {
		if (authorization != null) {
			request.setHeader("Authorization", authorization);
		}
	}

	protected CloseableHttpClient createClient() {
		return HttpClients.custom().build();
	}

	protected void closeClient(CloseableHttpClient httpClient) {
		try {
			httpClient.close();
		} catch (IOException quiet) {
		}
	}

	protected void handleError(CloseableHttpResponse response) {
		final int status = response.getStatusLine().getStatusCode();
		if (status == HttpStatus.INTERNAL_SERVER_ERROR_500) {
			try {
				ErrorMessage msg = new Gson().fromJson(EntityUtils.toString(response.getEntity()), ErrorMessage.class);
				throw new RestException(msg, status);
			} catch (JsonSyntaxException | IOException fallthru) {
			}
		}
		if (status < 200 || status > 299) { // Status 2xx is okay.
			throw new RestStatusException(status);
		}
	}
}
