package api.iteration2;

import api.BaseTest;
import constants.ResponseMessage;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.TransferRequest;
import models.TransferResponse;
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

import static constants.TransferTypes.TRANSFER_IN;
import static constants.TransferTypes.TRANSFER_OUT;
import static generators.RandomData.getRandomAccountId;
import static models.comparison.ModelAssertions.assertThatModels;
import static org.assertj.core.api.Assertions.assertThat;
import static requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;
import static requests.steps.UserSteps.getAccountById;

public class AccountTransferTest extends BaseTest {

    @ValueSource(doubles = {1, MAX_TRANSFER_AMOUNT - 1, MAX_TRANSFER_AMOUNT})
    @ParameterizedTest
    @DisplayName("Перевод между своими счетами")
    public void userCanTransferMoneyBetweenAccounts(double transferAmount) {
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
                RequestSpecs.authAsUser(user),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);

        assertThatModels(transferRequest, transferResponse).match();
        assertThat(transferResponse.getMessage()).isEqualTo(ResponseMessage.TRANSFER_SUCCESSFUL.getMessage());

        firstAccount = getAccountById(user, transferRequest.getSenderAccountId());
        secondAccount = getAccountById(user, transferRequest.getReceiverAccountId());

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
    public void userCanTransferMoneyToOtherAccounts() {
        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

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
                RequestSpecs.authAsUser(firstUser),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK()
        ).post(transferRequest);

        assertThatModels(transferRequest, transferResponse).match();
        assertThat(transferResponse.getMessage()).isEqualTo(ResponseMessage.TRANSFER_SUCCESSFUL.getMessage());

        firstAccount = getAccountById(firstUser, transferRequest.getSenderAccountId());
        secondAccount = getAccountById(secondUser, transferRequest.getReceiverAccountId());

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
    public void userCanNotTransferInvalidAmountBetweenAccounts(double transferAmount) {
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
                RequestSpecs.authAsUser(user),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_TRANSFER.getMessage())
        ).post(transferRequest);

        firstAccount = getAccountById(user, transferRequest.getSenderAccountId());
        secondAccount = getAccountById(user, transferRequest.getReceiverAccountId());

        assertThat(firstAccount.getBalance()).isZero();
        assertThat(firstAccount.getTransactions()).isEmpty();
        assertThat(secondAccount.getBalance()).isZero();
        assertThat(secondAccount.getTransactions()).isEmpty();
    }

    @Test
    @DisplayName("Отсутствие перевода между счетами при недостаточности средств у отправителя")
    public void userCanNotTransferInsufficientMoneyBetweenAccounts() {
        double depositAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        double transferAmount = depositAmount + 1;

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse senderAccount = UserSteps.createAccount(user);
        CreateAccountResponse recieverAccount = UserSteps.createAccount(user);
        UserSteps.depositAccount(user, senderAccount.getId(), depositAmount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccount.getId())
                .receiverAccountId(recieverAccount.getId())
                .amount(transferAmount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_TRANSFER.getMessage())
        ).post(transferRequest);

        senderAccount = getAccountById(user, transferRequest.getSenderAccountId());
        recieverAccount = getAccountById(user, transferRequest.getReceiverAccountId());

        assertThat(senderAccount.getBalance()).isEqualTo(depositAmount);
        assertThat(senderAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
        assertThat(recieverAccount.getBalance()).isZero();
        assertThat(recieverAccount.getTransactions()).isEmpty();
    }

    @Test
    @DisplayName("Отсутствие перевода на несуществующий счёт")
    public void userCanNotTransferToNotExistsAccounts() {
        int notExistsReceiverAccountId = getRandomAccountId();
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse senderAccount = UserSteps.createAccount(user);
        UserSteps.depositAccount(user, senderAccount.getId(), amount);

        TransferRequest transferRequest = TransferRequest.builder()
                .senderAccountId(senderAccount.getId())
                .receiverAccountId(notExistsReceiverAccountId)
                .amount(amount)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(ResponseMessage.INVALID_TRANSFER.getMessage())
        ).post(transferRequest);

        senderAccount = getAccountById(user, transferRequest.getSenderAccountId());

        assertThat(senderAccount.getBalance()).isEqualTo(transferRequest.getAmount());
        assertThat(senderAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
    }

    @Test
    @DisplayName("Перевод с чужого аккаунта на свой")
    public void userCanNotTransferFromNeSvoyAccount() {
        double amount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);

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
                RequestSpecs.authAsUser(firstUser),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsForbidden(ResponseMessage.UNAUTH_ACCESS_TO_ACCOUNT.getMessage())
        ).post(transferRequest);

        firstUserAccount = getAccountById(firstUser, transferRequest.getReceiverAccountId());
        secondUserAccount = getAccountById(secondUser, transferRequest.getSenderAccountId());

        assertThat(firstUserAccount.getBalance()).isZero();
        assertThat(firstUserAccount.getTransactions()).isEmpty();
        assertThat(secondUserAccount.getBalance()).isEqualTo(amount);
        assertThat(secondUserAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
    }
}