package iteration2.api;

import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.ApiVersion;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import db.entity.comparison.DaoAndModelAssertions;
import db.request.*;
import iteration1.api.BaseTest;
import api.models.CreateAccountResponse;
import api.models.TransferRequest;
import api.models.TransferResponse;
import constants.ResponseMessage;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import api.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

import static api.generators.RandomData.getRandomAccountId;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;
import static constants.TransferTypes.TRANSFER_IN;
import static constants.TransferTypes.TRANSFER_OUT;
import static api.models.comparison.ModelAssertions.assertThatModels;
import static db.request.Condition.equalTo;
import static db.request.FieldUpdate.field;
import static db.steps.AccountsTableSteps.updateAccountAmount;
import static db.steps.CustomerTableSteps.getUserByUsername;
import static db.steps.TransactionsTableSteps.getReceiverTransaction;
import static db.steps.TransactionsTableSteps.getSenderTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;

public class AccountTransferTest extends BaseTest {

    @ValueSource(doubles = {1, MAX_TRANSFER_AMOUNT - 1, MAX_TRANSFER_AMOUNT})
    @ParameterizedTest
    @DisplayName("Перевод между своими счетами")
    @UserSession(testType = TestType.API)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanTransferMoneyBetweenAccounts(double transferAmount) {
        CreateAccountResponse firstAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse secondAccount = SessionStorage.getSteps().createAccount();

        updateAccountAmount(firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        TransferResponse transferResponse = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);

        assertThatModels(transferRequest, transferResponse).match();
        assertThat(transferResponse.getMessage()).isEqualTo(ResponseMessage.TRANSFER_SUCCESSFUL.getMessage());

        DaoAndModelAssertions.assertThat(
                transferResponse, getSenderTransaction(firstAccount.getId(), secondAccount.getId())
        ).match();
    }

    @Test
    @DisplayName("Перевод на чужой счёт")
    @UserSession(testType = TestType.API, value = 2)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanTransferMoneyToOtherAccounts() {
        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse firstAccount = SessionStorage.getSteps(1).createAccount();
        CreateAccountResponse secondAccount = SessionStorage.getSteps(2).createAccount();

        updateAccountAmount(firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        TransferResponse transferResponse = new ValidatedCrudRequester<TransferResponse>(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);

        assertThatModels(transferRequest, transferResponse).match();
        assertThat(transferResponse.getMessage()).isEqualTo(ResponseMessage.TRANSFER_SUCCESSFUL.getMessage());

        DaoAndModelAssertions.assertThat(
                transferResponse, getSenderTransaction(firstAccount.getId(), secondAccount.getId())
        ).match();
    }

    @MethodSource("testdataproviders.TransferDataProvider#userCanNotTransferInvalidAmountBetweenAccountsSource")
    @ParameterizedTest
    @DisplayName("Трансфер невалидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.API)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotTransferInvalidAmountBetweenAccounts(double transferAmount, String errorMessage) {
        CreateAccountResponse firstAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse secondAccount = SessionStorage.getSteps().createAccount();

        updateAccountAmount(firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(errorMessage)
        ).post(transferRequest);

        secondAccount = SessionStorage.getSteps().getAccountById(transferRequest.getReceiverAccountId());

        assertThat(secondAccount.getBalance()).isZero();
        assertThat(secondAccount.getTransactions()).isEmpty();
        assertThat(getSenderTransaction(firstAccount.getId(), secondAccount.getId())).isNull();
    }

    @Test
    @DisplayName("Отсутствие перевода между счетами при недостаточности средств у отправителя")
    @UserSession(testType = TestType.API)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotTransferInsufficientMoneyBetweenAccounts() {
        double depositAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        double transferAmount = depositAmount + 1;

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse recieverAccount = SessionStorage.getSteps().createAccount();

        updateAccountAmount(senderAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccount.getId())
                .receiverAccountId(recieverAccount.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_TRANSFER.getMessage())
        ).post(transferRequest);

        assertThat(getSenderTransaction(senderAccount.getId(), recieverAccount.getId())).isNull();
    }

    @Test
    @DisplayName("Отсутствие перевода на несуществующий счёт")
    @UserSession(testType = TestType.API)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotTransferToNotExistsAccounts() {
        int notExistsReceiverAccountId = getRandomAccountId();
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        updateAccountAmount(senderAccount.getId(), amount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccount.getId())
                .receiverAccountId(notExistsReceiverAccountId)
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_TRANSFER.getMessage())
        ).post(transferRequest);

        assertThat(getSenderTransaction(senderAccount.getId(), notExistsReceiverAccountId)).isNull();
    }

    @Test
    @DisplayName("Перевод с чужого аккаунта на свой")
    @UserSession(testType = TestType.API, value = 2)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotTransferFromNeSvoyAccount() {
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse firstUserAccount = SessionStorage.getSteps(1).createAccount();
        CreateAccountResponse secondUserAccount = SessionStorage.getSteps(2).createAccount();

        updateAccountAmount(secondUserAccount.getId(), amount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(secondUserAccount.getId())
                .receiverAccountId(firstUserAccount.getId())
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsForbidden(ResponseMessage.UNAUTH_ACCESS_TO_ACCOUNT.getMessage())
        ).post(transferRequest);

        assertThat(getSenderTransaction(secondUserAccount.getId(), firstUserAccount.getId())).isNull();
    }
}