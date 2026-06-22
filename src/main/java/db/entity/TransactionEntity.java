package db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEntity extends BaseEntity {
    private Long id;
    private Double amount;
    private String type;
    private String timestamp;
    private Long accountId;
    private Long relatedAccountId;
}
