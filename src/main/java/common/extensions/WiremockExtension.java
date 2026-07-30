package common.extensions;

import api.mocks.MockBodyHandler;
import api.mocks.MockResponse;
import api.models.BaseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import common.annotations.mock.Mock;
import common.annotations.mock.MockBody;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class WiremockExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(WiremockExtension.class);
    private static final String MOCK_RESPONSE_KEY = "mockResponse";
    private WireMockServer wireMockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Mock.class)
                .ifPresent(mock -> {
                    Annotation bodyAnnotation = findMockBodyAnnotation(context);
                    MockResponse<? extends BaseModel> mockResponse = buildResponseFromAnnotation(bodyAnnotation);
                    saveMockResponse(context, mockResponse);
                    setupWireMock(mock, mockResponse);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        MockResponse<? extends BaseModel> mockResponse = getMockResponse(extensionContext);

        if (mockResponse == null || mockResponse.getBody() == null) {
            return false;
        }

        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType.isAssignableFrom(mockResponse.getBody().getClass());
    }

    @Override
    public @Nullable Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        MockResponse<? extends BaseModel> mockResponse = getMockResponse(extensionContext);

        if (mockResponse == null || mockResponse.getBody() == null) {
            throw new IllegalStateException("Mock response was not created before parameter resolving");
        }

        return mockResponse.getBody();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }

        context.getStore(NAMESPACE).remove(MOCK_RESPONSE_KEY);
    }

    private void setupWireMock(Mock config, MockResponse<? extends BaseModel> mockResponse) {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(config.port()));
        wireMockServer.start();
        WireMock.configureFor("0.0.0.0", config.port());

        String responseBody = toJson(mockResponse.getBody());

        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(mockResponse.getHttpStatus())
                .withBody(responseBody);

        mockResponse.getHeaders().forEach(responseBuilder::withHeader);

        wireMockServer.stubFor(post(urlPathMatching(config.endpoint().getUrl()))
                .willReturn(responseBuilder));
    }

    private Annotation findMockBodyAnnotation(ExtensionContext context) {
        Annotation[] annotations = context.getRequiredTestMethod().getAnnotations();

        List<Annotation> mockBodyAnnotations = Arrays.stream(annotations)
                .filter(annotation ->
                        annotation.annotationType().isAnnotationPresent(MockBody.class)
                )
                .toList();

        if (mockBodyAnnotations.isEmpty()) {
            throw new IllegalStateException(
                    "No mock body annotation found. Expected annotation marked with @MockBody"
            );
        }

        return mockBodyAnnotations.get(0);
    }

    @SuppressWarnings("unchecked")
    private MockResponse<? extends BaseModel> buildResponseFromAnnotation(Annotation bodyAnnotation) {
        MockBody mockBody = bodyAnnotation.annotationType().getAnnotation(MockBody.class);
        Class<? extends MockBodyHandler> handlerClass = mockBody.handler();

        try {
            MockBodyHandler<Annotation, ? extends BaseModel> handler =
                    (MockBodyHandler<Annotation, ? extends BaseModel>)
                            handlerClass.getDeclaredConstructor().newInstance();
            return handler.buildResponse(bodyAnnotation);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to build mock response for annotation = " + bodyAnnotation.annotationType().getName(), e
            );
        }
    }

    private static void saveMockResponse(ExtensionContext context, MockResponse<? extends BaseModel> mockResponse) {
        context.getStore(NAMESPACE).put(MOCK_RESPONSE_KEY, mockResponse);
    }

    @SuppressWarnings("unchecked")
    private static MockResponse<? extends BaseModel> getMockResponse(ExtensionContext context) {
        return context.getStore(NAMESPACE).get(MOCK_RESPONSE_KEY, MockResponse.class);
    }

    private String toJson(BaseModel model) {
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to serialize mock response model: " +
                            model.getClass().getName(),
                    e
            );
        }
    }
}
