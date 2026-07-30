package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionsResponse extends BaseModel {
    private long id;
    private double amount;
    private String type;
    private String timestamp;
    private long relatedAccountId;
    private String status;
    private boolean fraudCheckRequired;
}
