package iteration2.api;

import iteration1.api.BaseTest;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.TransferRequest;
import api.models.TransferResponse;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.Endpoint;
import api.requesters.CrudRequester;
import api.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static api.generators.RandomData.generateRandomAccountId;
import static api.models.comparison.ModelAssertions.assertThatModels;
import static org.assertj.core.api.Assertions.assertThat;
import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;

public class AccountTransferTest extends BaseTest {

    @ValueSource(doubles = {1, MAX_TRANSFER_AMOUNT - 1, MAX_TRANSFER_AMOUNT})
    @ParameterizedTest
    @DisplayName("Перевод между своими счетами")
    public void userCanTransferMoneyBetweenAccounts(double transferAmount) {
        String successfulMessage = "Transfer successful";

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse firstAccount = UserSteps.createAccount(user);
        CreateAccountResponse secondAccount = UserSteps.createAccount(user);

        UserSteps.depositAccount(user, firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        TransferResponse transferResponse = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);
        assertThatModels(transferRequest, transferResponse).match();
        assertThat(transferResponse.getMessage()).isEqualTo(successfulMessage);
    }

    @Test
    @DisplayName("Перевод на чужой счёт")
    public void userCanTransferMoneyToOtherAccounts() {
        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        String successfulMessage = "Transfer successful";

        CreateUserRequest firstUser = AdminSteps.createUser();
        CreateUserRequest secondUser = AdminSteps.createUser();
        CreateAccountResponse firstAccount = UserSteps.createAccount(firstUser);
        CreateAccountResponse secondAccount = UserSteps.createAccount(secondUser);
        UserSteps.depositAccount(firstUser, firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        TransferResponse transferResponse = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(firstUser.getUsername(), firstUser.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);
        assertThatModels(transferRequest, transferResponse).match();
        assertThat(transferResponse.getMessage()).isEqualTo(successfulMessage);
    }

    @ValueSource(doubles = {-1, 0, MAX_TRANSFER_AMOUNT + 1})
    @ParameterizedTest
    @DisplayName("Трансфер невалидной суммы на свой существующий счёт")
    public void userCanNotTransferInvalidAmountBetweenAccounts(double transferAmount) {
        String errorMessage = "Invalid transfer: insufficient funds or invalid accounts";

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse firstAccount = UserSteps.createAccount(user);
        CreateAccountResponse secondAccount = UserSteps.createAccount(user);

        UserSteps.depositAccount(user, firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(errorMessage)
        ).post(transferRequest);
    }

    @Test
    @DisplayName("Отсутствие перевода между счетами при недостаточности средств у отправителя")
    public void userCanNotTransferInsufficientMoneyBetweenAccounts() {
        double depositAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        double transferAmount = depositAmount + 1;
        String errorMessage = "Invalid transfer: insufficient funds or invalid accounts";

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse firstAccount = UserSteps.createAccount(user);
        CreateAccountResponse secondAccount = UserSteps.createAccount(user);
        UserSteps.depositAccount(user, firstAccount.getId(), depositAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(errorMessage)
        ).post(transferRequest);
    }

    @Test
    @DisplayName("Отсутствие перевода на несуществующий счёт")
    public void userCanNotTransferToNotExistsAccounts() {
        int notExistsAccountId = generateRandomAccountId();
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        String errorMessage = "Invalid transfer: insufficient funds or invalid accounts";

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse firstAccount = UserSteps.createAccount(user);
        UserSteps.depositAccount(user, firstAccount.getId(), amount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(notExistsAccountId)
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUsername(), user.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(errorMessage)
        ).post(transferRequest);
    }

    @Test
    @DisplayName("Перевод с чужого аккаунта на свой")
    public void userCanNotTransferFromNeSvoyAccount() {
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        String errorMessage = "Unauthorized access to account";

        CreateUserRequest firstUser = AdminSteps.createUser();
        CreateUserRequest secondUser = AdminSteps.createUser();
        CreateAccountResponse firstUserAccount = UserSteps.createAccount(firstUser);
        CreateAccountResponse secondUserAccount = UserSteps.createAccount(secondUser);
        UserSteps.depositAccount(secondUser, secondUserAccount.getId(), amount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(secondUserAccount.getId())
                .receiverAccountId(firstUserAccount.getId())
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(firstUser.getUsername(), firstUser.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsForbidden(errorMessage)
        ).post(transferRequest);
    }
}