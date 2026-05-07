package requests.skeleton.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get(long id);

    Object get();

    Object update(BaseModel baseModel);

    Object delete(long id);

    Object getAll(Class<?> clazz);
}
