package db.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountEntity extends BaseEntity {
    private Long id;
    private String accountNumber;
    private Double balance;
    private Long customerId;
}
