package api.mocks;

import api.models.mockmodel.FraudCheckResponse;
import common.annotations.mock.FraudMockBody;
import org.apache.http.HttpStatus;

import java.util.Map;


public class FraudMockBodyHandler implements MockBodyHandler<FraudMockBody, FraudCheckResponse> {

    @Override
    public MockResponse<FraudCheckResponse> buildResponse(FraudMockBody annotation) {
        FraudCheckResponse fraudCheckResponse = FraudCheckResponse.builder()
                .status(annotation.status())
                .decision(annotation.decision())
                .riskScore(annotation.riskScore())
                .reason(annotation.reason())
                .requiresManualReview(annotation.requiresManualReview())
                .additionalVerificationRequired(annotation.additionalVerificationRequired())
                .build();
        return new MockResponse<>(
                HttpStatus.SC_OK,
                Map.of("Content-Type", "application/json"),
                fraudCheckResponse
        );
    }

}
