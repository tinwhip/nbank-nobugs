package common.annotations.mock;

import api.mocks.FraudMockBodyHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@MockBody(handler = FraudMockBodyHandler.class)
public @interface FraudMockBody {
    String status() default "SUCCESS";

    String decision() default "APPROVED";

    double riskScore() default 0.2;

    String reason() default "Low risk transaction";

    boolean requiresManualReview() default false;

    boolean additionalVerificationRequired() default false;
}
