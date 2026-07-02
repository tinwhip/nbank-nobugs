package common.annotations.mock;

import api.mocks.MockEndpoint;
import common.extensions.WiremockExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(WiremockExtension.class)
public @interface Mock {
    /**
     * The WireMock port to use
     */
    int port() default 8080;

    /**
     * The endpoint path to mock
     */
    MockEndpoint endpoint();
}
