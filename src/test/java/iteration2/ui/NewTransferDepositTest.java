package iteration2.ui;

import api.models.CreateAccountResponse;
import common.TestType;
import common.annotations.ApiVersion;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.BankAlert;
import iteration1.ui.BaseUiTest;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ui.pages.TransferPage;

import java.util.Locale;

import static api.generators.RandomData.*;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;
import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;
import static constants.BankAlert.*;
import static constants.TransferTypes.TRANSFER_IN;
import static constants.TransferTypes.TRANSFER_OUT;
import static db.steps.AccountsTableSteps.updateAccountAmount;
import static db.steps.TransactionsTableSteps.getReceiverTransaction;
import static db.steps.TransactionsTableSteps.getSenderTransaction;
import static org.assertj.core.api.Assertions.assertThat;

public class NewTransferDepositTest extends BaseUiTest {

    @ValueSource(doubles = {1, MAX_TRANSFER_AMOUNT - 1, MAX_TRANSFER_AMOUNT})
    @ParameterizedTest
    @DisplayName("Перевод между своими счетами")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanTransferMoneyBetweenAccounts(double transferAmount) {
        CreateAccountResponse sendAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiveAccount = SessionStorage.getSteps().createAccount();
        String recipientName = SessionStorage.getUser().getUsername();
        String recipientAccountNumber = receiveAccount.getAccountNumber();

        updateAccountAmount(sendAccount.getId(), transferAmount);

        new TransferPage().open()
                .sendTransfer(
                        sendAccount.getId(),
                        recipientName,
                        recipientAccountNumber,
                        transferAmount
                )
                .checkAlertMessageAndAccept(
                        BankAlert.getFormattedMessageWithDouble(
                                TRANSFER_SUCCESSFULLY, transferAmount, recipientAccountNumber
                        )
                );
        assertThat(getSenderTransaction(sendAccount.getId(), receiveAccount.getId()).getAmount()).isZero();
        assertThat(getReceiverTransaction(receiveAccount.getId(), sendAccount.getId()).getAmount()).isEqualTo(transferAmount);
    }

    @Test
    @DisplayName("Перевод на чужой счёт")
    @UserSession(testType = TestType.UI, value = 2)
    @ApiVersion(version = "with_validation_fix")
    public void userCanTransferMoneyToOtherAccounts() {
        double transferAmount = MAX_TRANSFER_AMOUNT;

        CreateAccountResponse sendAccount = SessionStorage.getSteps(1).createAccount();
        CreateAccountResponse receiveAccount = SessionStorage.getSteps(2).createAccount();
        String recipientName = SessionStorage.getUser().getUsername();
        String recipientAccountNumber = receiveAccount.getAccountNumber();

        updateAccountAmount(sendAccount.getId(), transferAmount);

        new TransferPage().open()
                .sendTransfer(
                        sendAccount.getId(),
                        recipientName,
                        recipientAccountNumber,
                        transferAmount
                )
                .checkAlertMessageAndAccept(
                        BankAlert.getFormattedMessageWithDouble(
                                TRANSFER_SUCCESSFULLY, transferAmount, recipientAccountNumber
                        )
                );

        assertThat(getSenderTransaction(sendAccount.getId(), receiveAccount.getId()).getAmount()).isZero();
        assertThat(getReceiverTransaction(receiveAccount.getId(), sendAccount.getId()).getAmount()).isEqualTo(transferAmount);
    }

    @MethodSource("testdataproviders.TransferDataProvider#userCanNotTransferInvalidAmountBetweenAccountsSource")
    @ParameterizedTest
    @DisplayName("Трансфер невалидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferInvalidAmountBetweenAccounts(double transferAmount, String message) {
        CreateAccountResponse sendAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiveAccount = SessionStorage.getSteps().createAccount();
        String recipientName = SessionStorage.getUser().getUsername();
        String recipientAccountNumber = receiveAccount.getAccountNumber();

        updateAccountAmount(sendAccount.getId(), transferAmount);

        new TransferPage().open()
                .sendTransfer(
                        sendAccount.getId(),
                        recipientName,
                        recipientAccountNumber,
                        transferAmount
                )
                .checkAlertMessageAndAccept(message);

        receiveAccount = SessionStorage.getSteps().getAccountById(receiveAccount.getId());

        assertThat(receiveAccount.getBalance()).isZero();
        assertThat(receiveAccount.getTransactions()).isEmpty();
        assertThat(getSenderTransaction(sendAccount.getId(), receiveAccount.getId())).isNull();
    }

    @Test
    @DisplayName("Отсутствие перевода на несуществующий счёт")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferToNotExistsAccounts() {
        String notExistsReceiverAccountId = getRandomAccountNumber();
        double amount = getRandomDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        updateAccountAmount(senderAccount.getId(), amount);

        new TransferPage().open()
                .sendTransfer(
                        senderAccount.getId(),
                        getUsername(),
                        notExistsReceiverAccountId,
                        amount
                )
                .checkAlertMessageAndAccept(NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER.getMessage());

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());

        assertThat(senderAccount.getBalance()).isEqualTo(amount);
        assertThat(senderAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isNotEqualTo(TRANSFER_OUT.name())
        );
    }

    @Test
    @DisplayName("Отсутствие перевода без выбора аккаунта отправителя")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferWithoutSenderAccount() {
        double amount = getRandomDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiverAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), amount);

        new TransferPage().open()
                .enterRecipientName(getUsername())
                .enterRecipientAccountNumber(receiverAccount.getAccountNumber())
                .enterAmount(amount)
                .confirmDetails()
                .sendTransfer()
                .checkAlertMessageAndAccept(FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        assertThat(receiverAccount.getBalance()).isZero();
    }

    @Test
    @DisplayName("Перевод без Recipient Name")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferWithoutRecipientName() {
        double amount = getRandomDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiverAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), amount);

        new TransferPage().open()
                .selectAccount(senderAccount.getId())
                .enterRecipientAccountNumber(receiverAccount.getAccountNumber())
                .enterAmount(amount)
                .confirmDetails()
                .sendTransfer()
                .checkAlertMessageAndAccept(
                        BankAlert.getFormattedMessageWithDouble(
                                TRANSFER_SUCCESSFULLY, amount, receiverAccount.getAccountNumber()
                        )
                );

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        assertThat(senderAccount.getBalance()).isZero();
        assertThat(senderAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_OUT.name())
        );
        assertThat(receiverAccount.getBalance()).isEqualTo(amount);
        assertThat(receiverAccount.getTransactions()).anySatisfy(
                transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_IN.name())
        );
    }

    @Test
    @DisplayName("Отсутствие перевода без Recipient Account Number")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferWithoutRecipientAccountNumber() {
        double amount = getRandomDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiverAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), amount);

        new TransferPage().open()
                .selectAccount(senderAccount.getId())
                .enterRecipientName(SessionStorage.getUser().getUsername())
                .enterAmount(amount)
                .confirmDetails()
                .sendTransfer()
                .checkAlertMessageAndAccept(FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        assertThat(senderAccount.getBalance()).isEqualTo(amount);
        assertThat(receiverAccount.getBalance()).isZero();
    }

    @Test
    @DisplayName("Отсутствие перевода без Amount")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferWithoutAmount() {
        double amount = getRandomDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiverAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), amount);

        new TransferPage().open()
                .selectAccount(senderAccount.getId())
                .enterRecipientName(SessionStorage.getUser().getUsername())
                .enterRecipientAccountNumber(receiverAccount.getAccountNumber())
                .confirmDetails()
                .sendTransfer()
                .checkAlertMessageAndAccept(FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        assertThat(senderAccount.getBalance()).isEqualTo(amount);
        assertThat(receiverAccount.getBalance()).isZero();
    }

    @Test
    @DisplayName("Отсутствие перевода без Confirm")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanNotTransferWithoutConfirm() {
        double amount = getRandomDouble(1, MAX_TRANSFER_AMOUNT);

        CreateAccountResponse senderAccount = SessionStorage.getSteps().createAccount();
        CreateAccountResponse receiverAccount = SessionStorage.getSteps().createAccount();
        SessionStorage.getSteps().depositAccount(senderAccount.getId(), amount);

        new TransferPage().open()
                .selectAccount(senderAccount.getId())
                .enterRecipientName(SessionStorage.getUser().getUsername())
                .enterRecipientAccountNumber(receiverAccount.getAccountNumber())
                .enterAmount(amount)
                .sendTransfer()
                .checkAlertMessageAndAccept(FILL_ALL_FIELDS_AND_CONFIRM.getMessage());

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        assertThat(senderAccount.getBalance()).isEqualTo(amount);
        assertThat(receiverAccount.getBalance()).isZero();
    }

}
