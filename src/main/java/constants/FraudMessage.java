package constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FraudMessage {
    MANUAL_REVIEW_REQUIRED("Transfer requires manual review"),
    VERIFICATION_REQUIRED("Additional verification required"),
    SUCCESS("Transfer approved and processed immediately");

    private String message;
}
