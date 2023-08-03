package github.soltaufintel.amalia.rest;

import java.io.File;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.pmw.tinylog.Logger;

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
	
	/**
	 * Upload a .zip file.
	 * content type: APPLICATION_OCTET_STREAM,
	 * verb: POST
	 * @param zipFile -
	 */
    public void uploadZip(File zipFile) {
        HttpPost request = new HttpPost(uri);
        request.setEntity(new FileEntity(zipFile, ContentType.APPLICATION_OCTET_STREAM));
        doRequest(request).close();
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
		return request(request, new Gson().toJson(object), getJsonContentType());
	}
	
	/**
	 * You may want to set the content type to "application/json; charset=cp1252".
	 * @return JSON content type
	 */
	protected String getJsonContentType() {
	    return "application/json";
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
		return new RestResponse(response, client == null ? httpClient : null);
	}
	
	protected void initRequest(HttpRequestBase request) {
		if (authorization != null) {
			request.setHeader("Authorization", authorization);
		}
	}

	protected CloseableHttpClient createClient() {
		return HttpClients.custom().build();
	}

	protected void handleError(CloseableHttpResponse response) {
		final int status = response.getStatusLine().getStatusCode();
		if (status == HttpStatus.INTERNAL_SERVER_ERROR_500) {
			try {
			    String entityStr = EntityUtils.toString(response.getEntity());
			    Logger.debug("REST error response: " + entityStr);
                ErrorMessage msg = new Gson().fromJson(entityStr, ErrorMessage.class);
                if (msg != null) {
                    throw new RestException(msg, status);
                }
			} catch (JsonSyntaxException | IOException fallthru) {
			}
		}
		if (status < HttpStatus.OK_200 || status > 299) { // Status 2xx is okay.
			throw new RestStatusException(status);
		}
	}
}
