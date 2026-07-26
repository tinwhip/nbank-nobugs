package api.models.mockmodel;

import api.models.BaseModel;
import api.models.TransferResponse;
import constants.FraudMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class FraudCheckResponse extends BaseModel {
    private String status;
    private String decision;
    private double riskScore;
    private String reason;
    private boolean requiresManualReview;
    private boolean additionalVerificationRequired;

    public static TransferResponse buildExpectedTransferResponse(
            FraudCheckResponse mockResponse,
            String status,
            FraudMessage fraudMessage,
            double transferAmount,
            long senderAccountId,
            long receiverAccountId
    ) {
        return TransferResponse.builder()
                .status(status)
                .message(fraudMessage.getMessage())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(mockResponse.getRiskScore())
                .fraudReason(mockResponse.getReason())
                .requiresManualReview(mockResponse.isRequiresManualReview())
                .requiresVerification(mockResponse.isAdditionalVerificationRequired())
                .build();
    }
}
