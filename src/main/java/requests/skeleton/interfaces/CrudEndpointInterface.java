package requests.skeleton.interfaces;

import models.BaseModel;
import requests.skeleton.RequestParams;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get(RequestParams requestParams);

    Object get();

    Object update(BaseModel baseModel);

    Object delete(RequestParams requestParams);

    Object getAll(Class<?> clazz);

    Object getAll(RequestParams requestParams, Class<?> clazz);
}
