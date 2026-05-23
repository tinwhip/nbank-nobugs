package iteration2.api;

import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration1.api.BaseTest;
import api.models.CreateAccountResponse;
import api.models.TransferRequest;
import api.models.TransferResponse;
import constants.ResponseMessage;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import static api.generators.RandomData.getRandomAccountId;
import static constants.TransferTypes.TRANSFER_IN;
import static constants.TransferTypes.TRANSFER_OUT;
import static api.models.comparison.ModelAssertions.assertThatModels;
import static org.assertj.core.api.Assertions.assertThat;
import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;

public class AccountTransferTest extends BaseTest {

    @ValueSource(doubles = {1, MAX_TRANSFER_AMOUNT - 1, MAX_TRANSFER_AMOUNT})
    @ParameterizedTest
    @DisplayName("Перевод между своими счетами")
    @UserSession(testType = TestType.API)
    public void userCanTransferMoneyBetweenAccounts(double transferAmount) {
        CreateAccountResponse firstAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositAccount(firstAccount.getId(), transferAmount);

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

        firstAccount = SessionStorage.getSteps().getAccountById(transferRequest.getSenderAccountId());
        secondAccount = SessionStorage.getSteps().getAccountById(transferRequest.getReceiverAccountId());

        assertThat(firstAccount.getBalance()).isZero();
        assertThat(firstAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_OUT.name())
        );
        assertThat(secondAccount.getBalance()).isEqualTo(transferRequest.getAmount());
        assertThat(secondAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_IN.name())
        );
    }

    @Test
    @DisplayName("Перевод на чужой счёт")
    @UserSession(testType = TestType.API, value = 2)
    public void userCanTransferMoneyToOtherAccounts() {
        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse firstAccount = SessionStorage.getSteps(1).createAccount();
        CreateAccountResponse secondAccount = SessionStorage.getSteps(2).createAccount();
        SessionStorage.getSteps(1).depositAccount(firstAccount.getId(), transferAmount);

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

        firstAccount = SessionStorage.getSteps(1).getAccountById(transferRequest.getSenderAccountId());
        secondAccount = SessionStorage.getSteps(2).getAccountById(transferRequest.getReceiverAccountId());

        assertThat(firstAccount.getBalance()).isZero();
        assertThat(firstAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_OUT.name())
        );
        assertThat(secondAccount.getBalance()).isEqualTo(transferRequest.getAmount());
        assertThat(secondAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_IN.name())
        );
    }

    @ValueSource(doubles = {-1, 0, MAX_TRANSFER_AMOUNT + 1})
    @ParameterizedTest
    @DisplayName("Трансфер невалидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.API)
    public void userCanNotTransferInvalidAmountBetweenAccounts(double transferAmount) {
        CreateAccountResponse firstAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse secondAccount = SessionStorage.getSteps().createAccount();

        SessionStorage.getSteps().depositAccount(firstAccount.getId(), transferAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(firstAccount.getId())
                .receiverAccountId(secondAccount.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(SessionStorage.getUser(1)),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_TRANSFER.getMessage())
        ).post(transferRequest);

        firstAccount = SessionStorage.getSteps().getAccountById(transferRequest.getSenderAccountId());
        secondAccount = SessionStorage.getSteps().getAccountById(transferRequest.getReceiverAccountId());

        assertThat(firstAccount.getBalance()).isZero();
        assertThat(firstAccount.getTransactions()).isEmpty();
        assertThat(secondAccount.getBalance()).isZero();
        assertThat(secondAccount.getTransactions()).isEmpty();
    }

    @Test
    @DisplayName("Отсутствие перевода между счетами при недостаточности средств у отправителя")
    @UserSession(testType = TestType.API)
    public void userCanNotTransferInsufficientMoneyBetweenAccounts() {
        double depositAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        double transferAmount = depositAmount + 1;

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse recieverAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), depositAmount);

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

        senderAccount = SessionStorage.getSteps().getAccountById(transferRequest.getSenderAccountId());
        recieverAccount = SessionStorage.getSteps().getAccountById(transferRequest.getReceiverAccountId());

        assertThat(senderAccount.getBalance()).isEqualTo(depositAmount);
        assertThat(senderAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
        assertThat(recieverAccount.getBalance()).isZero();
        assertThat(recieverAccount.getTransactions()).isEmpty();
    }

    @Test
    @DisplayName("Отсутствие перевода на несуществующий счёт")
    @UserSession(testType = TestType.API)
    public void userCanNotTransferToNotExistsAccounts() {
        int notExistsReceiverAccountId = getRandomAccountId();
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), amount);

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

        senderAccount = SessionStorage.getSteps().getAccountById(transferRequest.getSenderAccountId());

        assertThat(senderAccount.getBalance()).isEqualTo(transferRequest.getAmount());
        assertThat(senderAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
    }

    @Test
    @DisplayName("Перевод с чужого аккаунта на свой")
    @UserSession(testType = TestType.API, value = 2)
    public void userCanNotTransferFromNeSvoyAccount() {
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse firstUserAccount = SessionStorage.getSteps(1).createAccount();
        CreateAccountResponse secondUserAccount = SessionStorage.getSteps(2).createAccount();
        SessionStorage.getSteps(2).depositAccount(secondUserAccount.getId(), amount);

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

        firstUserAccount = SessionStorage.getSteps(1).getAccountById(transferRequest.getReceiverAccountId());
        secondUserAccount = SessionStorage.getSteps(2).getAccountById(transferRequest.getSenderAccountId());

        assertThat(firstUserAccount.getBalance()).isZero();
        assertThat(firstUserAccount.getTransactions()).isEmpty();
        assertThat(secondUserAccount.getBalance()).isEqualTo(amount);
        assertThat(secondUserAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
    }
}