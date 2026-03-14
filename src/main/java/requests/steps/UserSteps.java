package requests.steps;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {

    public static final double MAX_DEPOSIT_AMOUNT = 5_000;
    public static final double MAX_TRANSFER_AMOUNT = 10_000;

    public static CreateAccountResponse createAccount(CreateUserRequest user) {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
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
                    RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                    Endpoint.ACCOUNTS_DEPOSIT,
                    ResponseSpecs.requestReturnsOK()
            ).post(deposit);

            remainingAmount = remainingAmount - partToDeposit;
        }

        return lastResponse;
    }

    public static CreateAccountResponse getAccountById(CreateUserRequest user, long accountId) {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).getAll().stream()
                .filter(accountInList -> accountId == accountInList.getId())
                .findAny().orElseThrow(
                        () -> new RuntimeException("No account with id = %s for customer".formatted(accountId))
                );
    }
}