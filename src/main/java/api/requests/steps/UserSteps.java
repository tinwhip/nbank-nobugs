package api.requests.steps;

import api.Endpoint;
import api.models.*;
import api.requests.skeleton.RequestParams;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.helpers.StepLogger;

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
        return StepLogger.log("Create account to user %s".formatted(username),
                () -> new ValidatedCrudRequester<CreateAccountResponse>(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.ACCOUNTS,
                        ResponseSpecs.entityWasCreated()
                ).post(null)
        );
    }

    public CreateAccountResponse depositAccount(long accountId, double amount) {
        return StepLogger.log("Deposit amount = %f to account id = %d".formatted(amount, accountId),
                () -> {
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
        );
    }

    public CreateAccountResponse getAccountById(long accountId) {
        return StepLogger.log("Get account by id = %d".formatted(accountId),
                () -> new ValidatedCrudRequester<CreateAccountResponse>(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.CUSTOMER_ACCOUNTS,
                        ResponseSpecs.requestReturnsOK()
                ).getAll(CreateAccountResponse[].class).stream()
                        .filter(accountInList -> accountId == accountInList.getId())
                        .findAny().orElseThrow(
                                () -> new RuntimeException("No account with id = %s for customer".formatted(accountId))
                        )
        );
    }

    public List<TransactionsResponse> getAllTransactionsByAccountId(long id) {
        return StepLogger.log("Get all transactions by account id = %d".formatted(id),
                () -> new ValidatedCrudRequester<TransactionsResponse>(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.ACCOUNT_TRANSACTIONS,
                        ResponseSpecs.requestReturnsOK()
                ).getAll(
                        RequestParams.params().path("id", id),
                        TransactionsResponse[].class
                )
        );
    }

    public List<CreateAccountResponse> getAllAccounts() {
        return StepLogger.log("User " + username + " get all accounts",
                () -> new ValidatedCrudRequester<CreateAccountResponse>(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.CUSTOMER_ACCOUNTS,
                        ResponseSpecs.requestReturnsOK()
                ).getAll(CreateAccountResponse[].class));
    }

    public TransferResponse transferBetweenAccounts(
            Long senderAccountId,
            Long receiverAccountId,
            double transferAmount
    ) {
        return StepLogger.log(
                "Transfer between accounts. Sender account = %d, Receiver account = %d, Amount = %f"
                        .formatted(senderAccountId, receiverAccountId, transferAmount),
                () -> {
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
        );
    }


    public GetCustomerProfileResponse getProfileInfo() {
        return StepLogger.log(
                "Get profile info by username = %s".formatted(username),
                () -> new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.GET_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).get()
        );
    }

    public GetCustomerProfileResponse changeProfileName(String name) {
        return StepLogger.log(
                "User with username = %s change profile name to '%s'".formatted(username, name),
                () -> new ValidatedCrudRequester<GetCustomerProfileResponse>(
                        RequestSpecs.authAsUser(username, password),
                        Endpoint.UPDATE_CUSTOMER_PROFILE,
                        ResponseSpecs.requestReturnsOK()
                ).update(new CustomerProfileRequest(name))
        );
    }

    public TransferResponse transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, double amount) {
        return StepLogger.log(
                "Transfer between accounts with fraud check. Sender account = %d, Receiver account = %d, Amount = %f"
                        .formatted(senderAccountId, receiverAccountId, amount),
                () -> {
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
        );
    }

}