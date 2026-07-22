package api.requests.steps;

import api.Endpoint;
import api.models.*;
import api.requests.skeleton.RequestParams;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class UserSteps {
    private String username;
    private String password;

    public UserSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static final double MAX_DEPOSIT_AMOUNT = 5_000;
    public static final double MAX_TRANSFER_AMOUNT = 10_000;

    public CreateAccountResponse createAccount() {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
    }

    public CreateAccountResponse depositAccount(long accountId, double amount) {
        double remainingAmount = amount;

        CreateAccountResponse lastResponse = null;

        while (remainingAmount > 0) {
            double partToDeposit = Math.min(remainingAmount, MAX_DEPOSIT_AMOUNT);
            DepositRequest deposit = new DepositRequest(accountId, partToDeposit);

            lastResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                    RequestSpecs.authAsUser(username, password),
                    Endpoint.ACCOUNTS_DEPOSIT,
                    ResponseSpecs.requestReturnsOK()
            ).post(deposit);

            remainingAmount = remainingAmount - partToDeposit;
        }

        return lastResponse;
    }

    public CreateAccountResponse getAccountById(long accountId) {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(CreateAccountResponse[].class).stream()
                .filter(accountInList -> accountId == accountInList.getId())
                .findAny().orElseThrow(
                        () -> new RuntimeException("No account with id = %s for customer".formatted(accountId))
                );
    }

    public List<TransactionsResponse> getAllTransactionsByAccountId(long id) {
        return new ValidatedCrudRequester<TransactionsResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.ACCOUNT_TRANSACTIONS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(
                RequestParams.params().path("id", id),
                TransactionsResponse[].class
        );
    }

    public List<CreateAccountResponse> getAllAccounts() {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(CreateAccountResponse[].class);
    }

    public TransferResponse transferBetweenAccounts(
            Long senderAccountId,
            Long receiverAccountId,
            double transferAmount
    ) {
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(transferAmount)
                .build();

        return new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);
    }

    public GetCustomerProfileResponse getProfileInfo() {
        return new ValidatedCrudRequester<GetCustomerProfileResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get();
    }

    public UpdateCustomerProfileResponse changeProfileName(String name) {
        return new ValidatedCrudRequester<UpdateCustomerProfileResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).update(new CustomerProfileRequest(name));
    }

    public TransferResponse transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, double amount) {
        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(amount)
                .description("Test transfer with fraud check")
                .build();

        return new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.TRANSFER_WITH_FRAUD_CHECK,
                ResponseSpecs.requestReturnsOK()).post(transferRequest);
    }

}