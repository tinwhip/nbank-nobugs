package api.requests.skeleton.interfaces;

import api.models.BaseModel;
import api.requests.skeleton.RequestParams;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get(RequestParams requestParams);

    Object get();

    Object update(BaseModel baseModel);

    Object delete(RequestParams requestParams);

    Object getAll(Class<?> clazz);

    Object getAll(RequestParams requestParams, Class<?> clazz);
}
