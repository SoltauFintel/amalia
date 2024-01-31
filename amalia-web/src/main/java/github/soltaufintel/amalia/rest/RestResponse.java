package github.soltaufintel.amalia.rest;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

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
     * Must only be called if these methods have not been called: response(), fromJson()
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

    /**
     * close() is called.
     * @return response String
     */
    public String response() {
        try {
            String ret = EntityUtils.toString(response.getEntity()); // must fetch result before closing http client
            close();
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * close() is called.
     * @param <T> any type
     * @param cls Java class for JSON data
     * @return object
     */
    public <T> T fromJson(Class<T> cls) {
        return new Gson().fromJson(response(), cls);
    }

    /**
     * close() is called.
     * @param <T> any type
     * @param type example: <code>java.lang.reflect.Type type = new TypeToken&lt;ArrayList&lt;TheItemClass&gt;&gt;() {}.getType();</code>
     * @return object
     */
    public <T> T fromJson(Type type) {
        return new Gson().fromJson(response(), type);
    }
}
