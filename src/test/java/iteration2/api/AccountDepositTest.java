package iteration2.api;

import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration1.api.BaseTest;
import api.models.CreateAccountResponse;
import api.models.DepositRequest;
import api.models.TransactionsResponse;
import constants.ResponseMessage;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static api.generators.RandomData.getRandomAccountId;
import static org.assertj.core.api.Assertions.assertThat;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;

public class AccountDepositTest extends BaseTest {

    @ValueSource(doubles = {1, MAX_DEPOSIT_AMOUNT - 1, MAX_DEPOSIT_AMOUNT})
    @ParameterizedTest
    @DisplayName("Депозит валидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.API)
    public void userCanDepositExistAccount(double amount) {
        CreateAccountResponse account = SessionStorage.getSteps().createAccount();

        //закинуть деньги на счёт
        CreateAccountResponse depositAccountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(
                new DepositRequest(account.getId(), amount)
        );
        assertThat(account.getId()).isEqualTo(depositAccountResponse.getId());
        assertThat(depositAccountResponse.getBalance()).isEqualTo(amount);
        assertThat(depositAccountResponse.getTransactions().get(0).getRelatedAccountId()).isEqualTo(account.getId());

        List<TransactionsResponse> transactions = SessionStorage.getSteps().getAllTransactionsByAccountId(account.getId());
        assertThat(transactions.size()).isEqualTo(1);
        assertThat(transactions.get(0)).isEqualTo(depositAccountResponse.getTransactions().get(0));

        //проверить что у пользователя есть транзакция с балансом
        CreateAccountResponse accountInfo = SessionStorage.getSteps().getAccountById(account.getId());
        assertThat(accountInfo.getBalance()).isEqualTo(amount);
    }

    @ValueSource(doubles = {0, -1, MAX_DEPOSIT_AMOUNT + 1})
    @ParameterizedTest
    @DisplayName("Депозит невалидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.API)
    public void userCanNotDepositExistAccountWithBadValue(double amount) {
        CreateAccountResponse account = SessionStorage.getSteps().createAccount();

        //закинуть деньги на счёт
        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_ACCOUNT_OR_AMOUNT.getMessage())
        ).post(
                new DepositRequest(account.getId(), amount)
        );

        //проверить, что у аккаунта не отображается транзакция
        List<TransactionsResponse> transactions = SessionStorage.getSteps().getAllTransactionsByAccountId(account.getId());
        assertThat(transactions.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Депозит на несуществующий счёт")
    @UserSession(testType = TestType.API)
    public void userCanNotDepositUnexistAccount() {
        int notExistsAccountId = getRandomAccountId();
        double amount = RandomUtils.nextDouble(1, MAX_DEPOSIT_AMOUNT);

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsForbidden(ResponseMessage.UNAUTH_ACCESS_TO_ACCOUNT.getMessage())
        ).post(
                new DepositRequest(notExistsAccountId, amount)
        );
    }

    @Test
    @DisplayName("Депозит на чужой счёт")
    @UserSession(testType = TestType.API, value = 2)
    public void userCanNotDepositOtherAccount() {
        double amount = RandomUtils.nextDouble(1, MAX_DEPOSIT_AMOUNT);
        CreateAccountResponse account = SessionStorage.getSteps(2).createAccount();

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsForbidden(ResponseMessage.UNAUTH_ACCESS_TO_ACCOUNT.getMessage())
        ).post(
                new DepositRequest(account.getId(), amount)
        );

        List<TransactionsResponse> transactions = SessionStorage.getSteps(2).getAllTransactionsByAccountId(account.getId());
        assertThat(transactions.size()).isEqualTo(0);
    }
}
