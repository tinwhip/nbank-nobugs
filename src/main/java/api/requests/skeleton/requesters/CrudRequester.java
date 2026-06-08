package api.requests.skeleton.requesters;

import api.Endpoint;
import api.HttpRequest;
import api.models.BaseModel;
import api.requests.skeleton.RequestParams;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "" : model;
        return given()
                .spec(requestSpecification)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get(RequestParams requestParams) {
        return given()
                .spec(requestSpecification)
                .pathParams(requestParams.getPathParams())
                .queryParams(requestParams.getQueryParams())
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse update(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete(RequestParams requestParams) {
        return given()
                .spec(requestSpecification)
                .pathParams(requestParams.getPathParams())
                .queryParams(requestParams.getQueryParams())
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getAll(Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getAll(RequestParams requestParams, Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .pathParams(requestParams.getPathParams())
                .queryParams(requestParams.getQueryParams())
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
