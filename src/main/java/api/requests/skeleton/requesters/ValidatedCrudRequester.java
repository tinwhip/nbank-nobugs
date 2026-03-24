package api.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.Endpoint;
import api.HttpRequest;
import api.interfaces.CrudEndpointInterface;
import api.interfaces.GetAllInterface;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ValidatedCrudRequester<M extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetAllInterface<M> {
    private CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public M post(BaseModel model) {
        return (M) crudRequester.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public M get(long id) {
        return (M) crudRequester.get(id).extract().as(endpoint.getResponseModel());
    }

    @Override
    public M get() {
        return (M) crudRequester.get().extract().as(endpoint.getResponseModel());
    }

    @Override
    public M update(BaseModel baseModel) {
        return (M) crudRequester.update(baseModel).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object delete(long id) {
        return (M) crudRequester.delete(id).extract().as(endpoint.getResponseModel());
    }

    @Override
    public List<M> getAll() {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getList("", (Class<M>) endpoint.getResponseModel());
    }

    @Override
    public List<M> getAll(Object id) {
        return given()
                .spec(requestSpecification)
                .pathParam("id", id)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getList("", (Class<M>) endpoint.getResponseModel());
    }
}
