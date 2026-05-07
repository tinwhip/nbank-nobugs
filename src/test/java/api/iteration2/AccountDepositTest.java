package api.iteration2;

import api.BaseTest;
import constants.ResponseMessage;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.TransactionsResponse;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.CrudRequester;
import requests.skeleton.requesters.ValidatedCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

import static generators.RandomData.generateRandomAccountId;
import static org.assertj.core.api.Assertions.assertThat;
import static requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;

public class AccountDepositTest extends BaseTest {

    @ValueSource(doubles = {1, MAX_DEPOSIT_AMOUNT - 1, MAX_DEPOSIT_AMOUNT})
    @ParameterizedTest
    @DisplayName("Депозит валидной суммы на свой существующий счёт")
    public void userCanDepositExistAccount(double amount) {
        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse account = UserSteps.createAccount(user);

        //закинуть деньги на счёт
        CreateAccountResponse depositAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(
                new DepositRequest(account.getId(), amount)
        );
        assertThat(account.getId()).isEqualTo(depositAccountResponse.getId());
        assertThat(depositAccountResponse.getBalance()).isEqualTo(amount);
        assertThat(depositAccountResponse.getTransactions().get(0).getRelatedAccountId()).isEqualTo(account.getId());

        List<TransactionsResponse> transactions = new ValidatedCrudRequester<TransactionsResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNT_TRANSACTIONS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(TransactionsResponse.class);
        assertThat(transactions.size()).isEqualTo(1);
        assertThat(transactions.get(0)).isEqualTo(depositAccountResponse.getTransactions().get(0));

        //проверить что у пользователя есть транзакция с балансом
        CreateAccountResponse accountInfo = UserSteps.getAccountById(user, account.getId());
        assertThat(accountInfo.getBalance()).isEqualTo(amount);
    }

    @ValueSource(doubles = {0, -1, MAX_DEPOSIT_AMOUNT + 1})
    @ParameterizedTest
    @DisplayName("Депозит невалидной суммы на свой существующий счёт")
    public void userCanNotDepositExistAccountWithBadValue(double amount) {
        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse account = UserSteps.createAccount(user);

        //закинуть деньги на счёт
        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_ACCOUNT_OR_AMOUNT.getMessage())
        ).post(
                new DepositRequest(account.getId(), amount)
        );

        //проверить, что у аккаунта не отображается транзакция
        List<TransactionsResponse> transactions = new ValidatedCrudRequester<TransactionsResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNT_TRANSACTIONS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(TransactionsResponse[].class);
        assertThat(transactions.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Депозит на несуществующий счёт")
    public void userCanNotDepositUnexistAccount() {
        int notExistsAccountId = generateRandomAccountId();
        double amount = RandomUtils.nextDouble(1, MAX_DEPOSIT_AMOUNT);
        CreateUserRequest user = AdminSteps.createUser();

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsForbidden(ResponseMessage.UNAUTH_ACCESS_TO_ACCOUNT.getMessage())
        ).post(
                new DepositRequest(notExistsAccountId, amount)
        );
    }

    @Test
    @DisplayName("Депозит на чужой счёт")
    public void userCanNotDepositOtherAccount() {
        double amount = RandomUtils.nextDouble(1, MAX_DEPOSIT_AMOUNT);
        CreateUserRequest firstUser = AdminSteps.createUser();
        CreateUserRequest secondUser = AdminSteps.createUser();
        CreateAccountResponse account = UserSteps.createAccount(secondUser);

        new CrudRequester(
                RequestSpecs.authAsUser(firstUser.getUsername(), firstUser.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsForbidden(ResponseMessage.UNAUTH_ACCESS_TO_ACCOUNT.getMessage())
        ).post(
                new DepositRequest(account.getId(), amount)
        );
    }
}
