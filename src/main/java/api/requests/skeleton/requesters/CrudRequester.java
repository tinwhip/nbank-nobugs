package api.requests.skeleton.requesters;

import api.Endpoint;
import api.HttpRequest;
import api.models.BaseModel;
import api.requests.skeleton.RequestParams;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import common.configs.Config;
import common.helpers.StepLogger;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    private final static String API_VERSION = Config.getProperty("apiVersion");

    public CrudRequester(
            RequestSpecification requestSpecification,
            Endpoint endpoint,
            ResponseSpecification responseSpecification
    ) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        return StepLogger.log("POST Request to " + endpoint.getUrl(),
                () -> {
                    var body = model == null ? "" : model;
                    return given()
                            .spec(requestSpecification)
                            .body(body)
                            .post(API_VERSION + endpoint.getUrl())
                            .then()
                            .spec(responseSpecification);
                }
        );
    }

    @Override
    public ValidatableResponse get(RequestParams requestParams) {
        return given()
                .spec(requestSpecification)
                .pathParams(requestParams.getPathParams())
                .queryParams(requestParams.getQueryParams())
                .get(API_VERSION + endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .get(API_VERSION + endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse update(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .put(API_VERSION + endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse delete(RequestParams requestParams) {
        return given()
                .spec(requestSpecification)
                .pathParams(requestParams.getPathParams())
                .queryParams(requestParams.getQueryParams())
                .delete(API_VERSION + endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getAll(Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .get(API_VERSION + endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getAll(RequestParams requestParams, Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .pathParams(requestParams.getPathParams())
                .queryParams(requestParams.getQueryParams())
                .get(API_VERSION + endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
