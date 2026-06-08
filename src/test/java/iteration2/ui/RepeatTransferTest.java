package iteration2.ui;

import api.models.CreateAccountResponse;
import api.models.TransactionsResponse;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.BankAlert;
import constants.TransferTypes;
import iteration1.ui.BaseUiTest;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ui.pages.TransferPage;

import java.util.Comparator;
import java.util.List;

import static api.generators.RandomData.getRandomDouble;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;
import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;
import static com.codeborne.selenide.Condition.*;
import static common.storage.SessionStorage.getSteps;
import static constants.BankAlert.INVALID_REPEAT_TRANSFER;
import static constants.BankAlert.SUCCESSFUL_REPEAT_TRANSFER;
import static constants.TransferTypes.TRANSFER_IN;
import static constants.TransferTypes.TRANSFER_OUT;
import static org.assertj.core.api.Assertions.assertThat;

public class RepeatTransferTest extends BaseUiTest {
    private static final double MAX_FIRST_AMOUNT_TEST = 100;
    private static final double MAX_SECOND_AMOUNT_TEST = 50;

    @Test
    @DisplayName("Повторный перевод на свой счёт")
    @UserSession(testType = TestType.UI)
    public void userCanRepeatTransferToOwnAccount() {
        double depositAmount = MAX_TRANSFER_AMOUNT;
        double firstTransferAmount = getRandomDouble(1, MAX_FIRST_AMOUNT_TEST);
        double secondTransferAmount = getRandomDouble(1, MAX_SECOND_AMOUNT_TEST);
        CreateAccountResponse senderAccount = getSteps().createAccount();
        CreateAccountResponse receiverAccount = getSteps().createAccount();

        getSteps().depositAccount(senderAccount.getId(), depositAmount);
        getSteps().transferBetweenAccounts(senderAccount.getId(), receiverAccount.getId(), firstTransferAmount);

        new TransferPage().open()

                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser().getUsername())

                .goToTransactionByTransferType(TRANSFER_IN)

                .openRepeatTransferWindow()
                .checkTransferToAccountId(senderAccount.getId())
                .repeatTransfer(senderAccount.getId(), secondTransferAmount)
                .checkAlertMessageAndAccept(
                        BankAlert.getFormattedMessageWithDouble(
                                SUCCESSFUL_REPEAT_TRANSFER,
                                secondTransferAmount,
                                senderAccount.getId(),
                                receiverAccount.getId()
                        )
                );

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        List<TransactionsResponse> senderTransferOutTransactions = senderAccount.getTransactions()
                .stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER_OUT.name()))
                .sorted(Comparator.comparingLong(TransactionsResponse::getId))
                .toList();

        List<TransactionsResponse> receiverTransferInTransactions = receiverAccount.getTransactions()
                .stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER_IN.name()))
                .sorted(Comparator.comparingLong(TransactionsResponse::getId))
                .toList();

        assertThat(senderTransferOutTransactions.size()).isEqualTo(2);
        assertThat(receiverTransferInTransactions.size()).isEqualTo(2);

        assertThat(senderTransferOutTransactions.get(1).getAmount()).isEqualTo(secondTransferAmount);
        assertThat(receiverTransferInTransactions.get(1).getAmount()).isEqualTo(secondTransferAmount);
    }

    @Test
    @DisplayName("Повторный перевод на чужой счёт")
    @UserSession(testType = TestType.UI, value = 2)
    public void userCanSearchTransactionBetweenOtherTransfer() {
        double depositAmount = MAX_TRANSFER_AMOUNT;
        double firstTransferAmount = getRandomDouble(1, MAX_FIRST_AMOUNT_TEST);
        double secondTransferAmount = getRandomDouble(1, MAX_SECOND_AMOUNT_TEST);
        CreateAccountResponse senderAccount = getSteps(1).createAccount();
        CreateAccountResponse receiverAccount = getSteps(2).createAccount();

        getSteps(1).depositAccount(senderAccount.getId(), depositAmount);
        getSteps(1).transferBetweenAccounts(senderAccount.getId(), receiverAccount.getId(), firstTransferAmount);

        new TransferPage().open()

                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser(2).getUsername())

                .goToTransactionByTransferType(TRANSFER_IN)

                .openRepeatTransferWindow()
                .checkTransferToAccountId(senderAccount.getId())
                .repeatTransfer(senderAccount.getId(), secondTransferAmount)
                .checkAlertMessageAndAccept(
                        BankAlert.getFormattedMessageWithDouble(
                                SUCCESSFUL_REPEAT_TRANSFER,
                                secondTransferAmount,
                                senderAccount.getId(),
                                receiverAccount.getId()
                        )
                );

        senderAccount = SessionStorage.getSteps(1).getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps(2).getAccountById(receiverAccount.getId());

        List<TransactionsResponse> senderTransferOutTransactions = senderAccount.getTransactions()
                .stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER_OUT.name()))
                .sorted(Comparator.comparingLong(TransactionsResponse::getId))
                .toList();

        List<TransactionsResponse> receiverTransferInTransactions = receiverAccount.getTransactions()
                .stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER_IN.name()))
                .sorted(Comparator.comparingLong(TransactionsResponse::getId))
                .toList();

        assertThat(senderTransferOutTransactions.size()).isEqualTo(2);
        assertThat(receiverTransferInTransactions.size()).isEqualTo(2);

        assertThat(senderTransferOutTransactions.get(1).getAmount()).isEqualTo(secondTransferAmount);
        assertThat(receiverTransferInTransactions.get(1).getAmount()).isEqualTo(secondTransferAmount);
    }

    @Test
    @DisplayName("Не активность кнопки Send Transfer без выбранного аккаунта")
    @UserSession(testType = TestType.UI)
    public void userCanNotRepeatTransferWithoutSelectAccount() {
        double depositAmount = MAX_TRANSFER_AMOUNT;
        double firstTransferAmount = getRandomDouble(1, MAX_FIRST_AMOUNT_TEST);
        CreateAccountResponse senderAccount = getSteps().createAccount();
        CreateAccountResponse receiverAccount = getSteps().createAccount();

        getSteps().depositAccount(senderAccount.getId(), depositAmount);
        getSteps().transferBetweenAccounts(senderAccount.getId(), receiverAccount.getId(), firstTransferAmount);

        new TransferPage().open()

                .transferAgain()
                .searchTransactions()

                .goToTransactionByTransferType(TRANSFER_IN)

                .openRepeatTransferWindow()
                .enterAmount(firstTransferAmount)
                .confirmDetails()
                .getSendTransferButton().shouldNot(clickable);
    }

    @Test
    @DisplayName("Не активность кнопки Send Transfer без подтверждения деталей")
    @UserSession(testType = TestType.UI)
    public void userCanNotRepeatTransferWithoutConfirm() {
        double depositAmount = MAX_TRANSFER_AMOUNT;
        double firstTransferAmount = getRandomDouble(1, MAX_FIRST_AMOUNT_TEST);
        CreateAccountResponse senderAccount = getSteps().createAccount();
        CreateAccountResponse receiverAccount = getSteps().createAccount();

        getSteps().depositAccount(senderAccount.getId(), depositAmount);
        getSteps().transferBetweenAccounts(senderAccount.getId(), receiverAccount.getId(), firstTransferAmount);

        new TransferPage().open()

                .transferAgain()
                .searchTransactions()

                .goToTransactionByTransferType(TRANSFER_IN)

                .openRepeatTransferWindow()
                .selectAccount(senderAccount.getId())
                .enterAmount(firstTransferAmount)
                .getSendTransferButton().shouldNot(clickable);
    }

    @ValueSource(doubles = {-1, 0, MAX_TRANSFER_AMOUNT + 1})
    @ParameterizedTest
    @DisplayName("Отсутствие перевода с некорректным Amount")
    @UserSession(testType = TestType.UI)
    public void userCanNotRepeatTransferWithInvalidAmount(double secondTransferAmount) {
        double depositAmount = MAX_TRANSFER_AMOUNT;
        double firstTransferAmount = getRandomDouble(1, MAX_FIRST_AMOUNT_TEST);
        CreateAccountResponse senderAccount = getSteps().createAccount();
        CreateAccountResponse receiverAccount = getSteps().createAccount();

        getSteps().depositAccount(senderAccount.getId(), depositAmount);
        getSteps().transferBetweenAccounts(senderAccount.getId(), receiverAccount.getId(), firstTransferAmount);

        new TransferPage().open()

                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser().getUsername())

                .goToTransactionByTransferType(TRANSFER_IN)

                .openRepeatTransferWindow()
                .checkTransferToAccountId(senderAccount.getId())
                .repeatTransfer(senderAccount.getId(), secondTransferAmount)
                .checkAlertMessageAndAccept(INVALID_REPEAT_TRANSFER.getMessage());

        senderAccount = SessionStorage.getSteps().getAccountById(senderAccount.getId());
        receiverAccount = SessionStorage.getSteps().getAccountById(receiverAccount.getId());

        List<TransactionsResponse> senderTransferOutTransactions = senderAccount.getTransactions()
                .stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER_OUT.name()))
                .sorted(Comparator.comparingLong(TransactionsResponse::getId))
                .toList();

        List<TransactionsResponse> receiverTransferInTransactions = receiverAccount.getTransactions()
                .stream()
                .filter(transaction -> transaction.getType().equals(TRANSFER_IN.name()))
                .sorted(Comparator.comparingLong(TransactionsResponse::getId))
                .toList();

        assertThat(senderTransferOutTransactions.size()).isEqualTo(1);
        assertThat(receiverTransferInTransactions.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Отмена повторного перевода")
    @UserSession(testType = TestType.UI)
    public void userCanCancelRepeatTransfer() {
        double depositAmount = MAX_TRANSFER_AMOUNT;
        double firstTransferAmount = getRandomDouble(1, MAX_FIRST_AMOUNT_TEST);
        CreateAccountResponse senderAccount = getSteps().createAccount();
        CreateAccountResponse receiverAccount = getSteps().createAccount();

        getSteps().depositAccount(senderAccount.getId(), depositAmount);
        getSteps().transferBetweenAccounts(senderAccount.getId(), receiverAccount.getId(), firstTransferAmount);

        new TransferPage().open()

                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser().getUsername())

                .goToTransactionByTransferType(TRANSFER_IN)

                .openRepeatTransferWindow()
                .cancelTransfer()
                .getSearchTransactionsButton().should(interactable).should(enabled);
    }
}
