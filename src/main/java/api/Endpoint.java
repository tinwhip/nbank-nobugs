package api;

import api.models.mockmodel.FraudCheckResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import api.models.*;

@AllArgsConstructor
@Getter
public enum Endpoint {
    ADMIN_USER("/admin/users", CreateUserRequest.class, CreateUserResponse.class),
    ACCOUNTS("/accounts", BaseModel.class, CreateAccountResponse.class),
    LOGIN("/auth/login", LoginUserRequest.class, LoginUserResponse.class),
    CUSTOMER_ACCOUNTS("/customer/accounts", BaseModel.class, CreateAccountResponse.class),
    ACCOUNTS_DEPOSIT("/accounts/deposit", DepositRequest.class, CreateAccountResponse.class),
    ACCOUNTS_TRANSFER("/accounts/transfer", TransferRequest.class, TransferResponse.class),
    ACCOUNT_TRANSACTIONS("/accounts/{id}/transactions", BaseModel.class, TransactionsResponse.class),
    UPDATE_CUSTOMER_PROFILE("/customer/profile", CustomerProfileRequest.class, GetCustomerProfileResponse.class),
    GET_CUSTOMER_PROFILE("/customer/profile", CustomerProfileRequest.class, GetCustomerProfileResponse.class),

    TRANSFER_WITH_FRAUD_CHECK("/accounts/transfer-with-fraud-check", TransferRequest.class, TransferResponse.class),
    FRAUD_CHECK_STATUS("/api/v1/accounts/fraud-check/{transactionId}", BaseModel.class, FraudCheckResponse.class);

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
