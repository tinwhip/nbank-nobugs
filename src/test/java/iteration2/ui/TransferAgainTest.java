package iteration2.ui;

import api.models.CreateAccountResponse;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.TransferPage;

import static api.generators.RandomData.getRandomDouble;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;
import static common.storage.SessionStorage.getSteps;
import static constants.TransferTypes.*;

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

        new TransferPage().open()
                .transferAgain()
                .searchTransactionsByUsernameOrName(SessionStorage.getUser().getUsername())
                .checkTransactionsContainsTypes(TRANSFER_OUT, TRANSFER_IN, DEPOSIT)
                .checkAllTransactionsHaveAmount(transferAmount)
                .checkAllTransactionsHaveFoundUnder(SessionStorage.getUser().getUsername());
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

        new TransferPage().open().transferAgain()

                .searchTransactionsByUsernameOrName(SessionStorage.getUser(1).getUsername())
                .checkTransactionsContainsTypes(TRANSFER_OUT, DEPOSIT)
                .checkAllTransactionsHaveAmount(transferAmount)
                .checkAllTransactionsHaveFoundUnder(SessionStorage.getUser(1).getUsername())

                .searchTransactionsByUsernameOrName(SessionStorage.getUser(2).getUsername())
                .checkTransactionsSize(1)
                .checkAllTransactionsHaveTypes(TRANSFER_IN)
                .checkAllTransactionsHaveFoundUnder(SessionStorage.getUser(2).getUsername());
    }
}
