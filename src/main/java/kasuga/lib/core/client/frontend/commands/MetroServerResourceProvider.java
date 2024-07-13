package kasuga.lib.core.client.frontend.commands;

import kasuga.lib.core.addons.resource.ResourceProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class MetroServerResourceProvider implements ResourceProvider {

    protected static final CloseableHttpClient client = HttpClients.createDefault();

    @Override
    public InputStream open(String path) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try{
            httpResponse = performRequest(HttpGet::new, path);
            return httpResponse.getEntity().getContent();
        }catch (IOException e){
            httpResponse.close();
            throw e;
        }
    }

    @Override
    public boolean exists(String path) {
        try(CloseableHttpResponse httpResponse = performRequest(HttpHead::new, path)){
            return httpResponse.getStatusLine().getStatusCode() == 200;
        }catch (RuntimeException | IOException e){
            throw new RuntimeException(e);
        }
    }

    protected CloseableHttpResponse performRequest(Function<String, HttpRequestBase> method, String path){
        HttpRequestBase headRequest = method.apply(createUri(path));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(headRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private String createUri(String path) {
        return "http://localhost:8081/"+ (path.startsWith("/") ? "" : "/") + path;
    }

    @Override
    public boolean isRegularFile(String path) {
        return !path.endsWith("/");
    }

    @Override
    public boolean isDirectory(String path) {
        return path.endsWith("/");
    }
}
