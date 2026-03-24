package api;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class HttpRequest {
    protected RequestSpecification requestSpecification;
    protected Endpoint endpoint;
    protected ResponseSpecification responseSpecification;
}
