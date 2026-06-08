package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCustomerProfileResponse extends BaseModel {
    private String password;
    private String role;
    private String name;
    private Integer id;
    private List<CreateAccountResponse> accounts;
    private String username;
}