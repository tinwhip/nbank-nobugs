package api.requests.skeleton.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object get(long id);

    Object get();

    Object update(BaseModel baseModel);

    Object delete(long id);
}
