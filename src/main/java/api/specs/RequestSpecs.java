package api.specs;

import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import common.configs.Config;
import api.models.CreateUserRequest;
import api.requests.skeleton.requesters.CrudRequester;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.LoginUserRequest;
import api.Endpoint;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;

public class RequestSpecs {
    private static Map<String, String> authHeaders = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter(),
                        new AllureRestAssured(),
                        new SwaggerCoverageRestAssured(
                                new FileSystemOutputWriter(Paths.get("target/" + OUTPUT_DIRECTORY))
                        )
                ))
                .setBaseUri(Config.getProperty("apiBaseUrl"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return defaultRequestBuilder()
                .addHeader("Authorization", getUserAuthHeader(username, password))
                .build();
    }

    public static String getUserAuthHeader(String username, String password) {
        String userAuthHeader;
        if (!authHeaders.containsKey(username)) {
            LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                    .username(username)
                    .password(password)
                    .build();
            userAuthHeader = new CrudRequester(
                    RequestSpecs.unauthSpec(),
                    Endpoint.LOGIN,
                    api.specs.ResponseSpecs.requestReturnsOK()
            )
                    .post(loginUserRequest)
                    .extract()
                    .header("Authorization");
            authHeaders.put(username, userAuthHeader);
        } else {
            userAuthHeader = authHeaders.get(username);
        }

        return userAuthHeader;
    }

    public static RequestSpecification authAsUser(CreateUserRequest user) {
        return authAsUser(user.getUsername(), user.getPassword());
    }
}
