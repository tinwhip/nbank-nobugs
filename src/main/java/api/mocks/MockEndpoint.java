package api.mocks;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MockEndpoint {
    FRAUD_CHECK("/fraud-check");
    private final String url;
}
