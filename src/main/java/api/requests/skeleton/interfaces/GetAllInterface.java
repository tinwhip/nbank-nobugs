package api.requests.skeleton.interfaces;

import api.models.BaseModel;

import java.util.List;

public interface GetAllInterface<T extends BaseModel> {
    List<T> getAll();

    List<T> getAll(Object id);
}
