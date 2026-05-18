package api.requests.skeleton.requesters;

import api.requests.skeleton.interfaces.CrudEndpointInterface;
import api.requests.skeleton.interfaces.GetAllEndpointInterface;
import api.requests.skeleton.interfaces.GetAllInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.Endpoint;
import api.HttpRequest;
import models.BaseModel;
import requests.skeleton.Endpoint;
import requests.skeleton.HttpRequest;
import requests.skeleton.RequestParams;
import requests.skeleton.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ValidatedCrudRequester<M extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
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
    public M get(RequestParams requestParams) {
        return (M) crudRequester.get(requestParams).extract().as(endpoint.getResponseModel());
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
    public Object delete(RequestParams requestParams) {
        return (M) crudRequester.delete(requestParams).extract().as(endpoint.getResponseModel());
    }

    @Override
    public List<M> getAll(Class<?> clazz) {
        M[] array = (M[]) crudRequester.getAll(clazz).extract().as(clazz);
        return Arrays.asList(array);
    }

    @Override
    public List<M> getAll(RequestParams requestParams, Class<?> clazz) {
        M[] array = (M[]) crudRequester.getAll(requestParams, clazz).extract().as(clazz);
        return Arrays.asList(array);
    }
}
