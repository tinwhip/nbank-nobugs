package api.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.Endpoint;
import api.HttpRequest;
import api.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.*;

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
    public ValidatableResponse get(long id) {
        return given()
                .spec(requestSpecification)
                .pathParam("id", id)
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
    public ValidatableResponse delete(long id) {
        return given()
                .spec(requestSpecification)
                .pathParam("id", id)
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
