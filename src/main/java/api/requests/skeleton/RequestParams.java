package api.requests.skeleton;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RequestParams {
    private final Map<String, Object> pathParams = new ConcurrentHashMap<>();
    private final Map<String, Object> queryParams = new ConcurrentHashMap<>();

    public static RequestParams params() {
        return new RequestParams();
    }

    public RequestParams path(String path, Object value) {
        pathParams.put(path, value);
        return this;
    }

    public RequestParams query(String path, Object value) {
        queryParams.put(path, value);
        return this;
    }
}
