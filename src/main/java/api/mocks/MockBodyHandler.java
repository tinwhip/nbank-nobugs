package api.mocks;

import api.models.BaseModel;

import java.lang.annotation.Annotation;

public interface MockBodyHandler<A extends Annotation, M extends BaseModel> {
    MockResponse<M> buildResponse(A annotation);
}
