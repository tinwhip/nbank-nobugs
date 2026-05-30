package iteration2.ui;

import api.models.CreateAccountResponse;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.elements.Transaction;
import ui.pages.TransferPage;

import java.util.List;

import static api.generators.RandomData.getRandomDouble;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;
import static common.storage.SessionStorage.getSteps;
import static constants.TransferTypes.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferAgainTest extends BaseUiTest {

    @Test
    @DisplayName("Отображение истории транзакций между своими счетами")
    @UserSession(testType = TestType.UI)
    public void userCanSearchTransactionBetweenOwnTransfers() {
        double transferAmount = getRandomDouble(1, MAX_DEPOSIT_AMOUNT);
        CreateAccountResponse firstAccount = getSteps().createAccount();
        CreateAccountResponse secondAccount = getSteps().createAccount();

        getSteps().depositAccount(firstAccount.getId(), transferAmount);
        getSteps().transferBetweenAccounts(firstAccount.getId(), secondAccount.getId(), transferAmount);

        List<Transaction> transactions = new TransferPage().open()
                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser().getUsername())
                .getAllTransactions();
        assertThat(transactions)
                .anySatisfy(
                        transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_OUT.name())
                )
                .anySatisfy(
                        transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_IN.name())
                )
                .anySatisfy(
                        transaction -> assertThat(transaction.getType()).isEqualTo(DEPOSIT.name())
                )
                .allSatisfy(
                        transaction -> assertThat(transaction.getAmount()).isEqualTo(transferAmount)
                )
                .allSatisfy(
                        transaction -> assertThat(transaction.getFoundUnder()).isEqualTo(SessionStorage.getUser().getUsername())
                );
    }

    @Test
    @DisplayName("Отображение истории перевода на чужой счёт")
    @UserSession(testType = TestType.UI, value = 2, auth = 1)
    public void userCanSearchTransactionBetweenOtherTransfer() {
        double transferAmount = getRandomDouble(1, MAX_DEPOSIT_AMOUNT);
        CreateAccountResponse firstAccount = getSteps(1).createAccount();
        CreateAccountResponse secondAccount = getSteps(2).createAccount();

        getSteps(1).depositAccount(firstAccount.getId(), transferAmount);
        getSteps(1).transferBetweenAccounts(firstAccount.getId(), secondAccount.getId(), transferAmount);

        List<Transaction> senderAccountTransactions = new TransferPage().open()
                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser(1).getUsername())
                .getAllTransactions();
        assertThat(senderAccountTransactions)
                .anySatisfy(
                        transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_OUT.name())
                )
                .anySatisfy(
                        transaction -> assertThat(transaction.getType()).isEqualTo(DEPOSIT.name())
                )
                .allSatisfy(
                        transaction -> assertThat(transaction.getAmount()).isEqualTo(transferAmount)
                )
                .allSatisfy(
                        transaction -> assertThat(transaction.getFoundUnder()).isEqualTo(SessionStorage.getUser(1).getUsername())
                );

        List<Transaction> receiverAccountTransactions = new TransferPage().open()
                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser(2).getUsername())
                .getAllTransactions();
        assertThat(receiverAccountTransactions.size()).isEqualTo(1);

        assertThat(receiverAccountTransactions)
                .allSatisfy(
                        transaction -> assertThat(transaction.getType()).isEqualTo(TRANSFER_IN.name())
                )
                .allSatisfy(
                        transaction -> assertThat(transaction.getAmount()).isEqualTo(transferAmount)
                )
                .allSatisfy(
                        transaction -> assertThat(transaction.getFoundUnder()).isEqualTo(SessionStorage.getUser(2).getUsername())
                );
    }
}
