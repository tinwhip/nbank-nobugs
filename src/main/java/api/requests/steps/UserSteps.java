package api.requests.steps;

import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.DepositRequest;
import api.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.TransactionsResponse;
import requests.skeleton.Endpoint;
import requests.skeleton.RequestParams;
import requests.skeleton.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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

    public static CreateAccountResponse createAccount(CreateUserRequest user) {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
    }

    public static CreateAccountResponse depositAccount(CreateUserRequest user, long accountId, double amount) {
        double remainingAmount = amount;

        CreateAccountResponse lastResponse = null;

        while (remainingAmount > 0) {
            double partToDeposit = Math.min(remainingAmount, MAX_DEPOSIT_AMOUNT);
            DepositRequest deposit = new DepositRequest(accountId, partToDeposit);

            lastResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                    RequestSpecs.authAsUser(user),
                    Endpoint.ACCOUNTS_DEPOSIT,
                    ResponseSpecs.requestReturnsOK()
            ).post(deposit);

            remainingAmount = remainingAmount - partToDeposit;
        }

        return lastResponse;
    }

    public static CreateAccountResponse getAccountById(CreateUserRequest user, long accountId) {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(CreateAccountResponse[].class).stream()
                .filter(accountInList -> accountId == accountInList.getId())
                .findAny().orElseThrow(
                        () -> new RuntimeException("No account with id = %s for customer".formatted(accountId))
                );
    }

    public static List<TransactionsResponse> getAllTransactionsByAccountId(CreateUserRequest user, long id) {
        return new ValidatedCrudRequester<TransactionsResponse>(
                RequestSpecs.authAsUser(user),
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
}