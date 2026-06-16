package iteration2.ui;

import api.models.CreateAccountResponse;
import common.TestType;
import common.annotations.ApiVersion;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.BankAlert;
import iteration1.ui.BaseUiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ui.elements.AccountElement;
import ui.pages.DepositMoney;

import java.util.Random;
import java.util.stream.Stream;

import static api.generators.RandomData.getRandomDouble;
import static api.requests.steps.UserSteps.MAX_DEPOSIT_AMOUNT;
import static db.steps.AccountsTableSteps.getAccountById;
import static org.assertj.core.api.Assertions.assertThat;
import static constants.BankAlert.*;

public class AccountDepositTest extends BaseUiTest {

    @ValueSource(doubles = {1, MAX_DEPOSIT_AMOUNT - 1, MAX_DEPOSIT_AMOUNT})
    @ParameterizedTest
    @DisplayName("Депозит валидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_validation_fix")
    public void userCanDepositToExistAccount(double amount) {
        CreateAccountResponse account = SessionStorage.getSteps().createAccount();
         new DepositMoney().open()
                .depositMoneyToAccount(account.getId(), amount)
                .checkAlertMessageAndAccept(
                        BankAlert.getFormattedMessageWithDouble(DEPOSIT_SUCCESSFULLY, amount, account.getAccountNumber())
                )
                .open()
                .getAccountSelector()
                .checkAccountHasBalance(account.getAccountNumber(), amount);

        double balance = SessionStorage.getSteps().getAccountById(account.getId()).getBalance();
        assertThat(balance).isEqualTo(amount);
    }

    private static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, ENTER_A_VALID_AMOUNT.getMessage()),
                Arguments.of(-1, ENTER_A_VALID_AMOUNT.getMessage()),
                Arguments.of(MAX_DEPOSIT_AMOUNT + 1, DEPOSIT_LESS_OR_EQUAL_TO.getMessage())
        );
    }
    @MethodSource("invalidAmount")
    @ParameterizedTest
    @DisplayName("Депозит невалидной суммы на свой существующий счёт")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotDepositExistAccountWithBadValue(double amount, String alert) {
        CreateAccountResponse account = SessionStorage.getSteps().createAccount();
        new DepositMoney().open()
                .depositMoneyToAccount(account.getId(), amount)
                .checkAlertMessageAndAccept(alert)
                .open()
                .getAccountSelector()
                .checkAccountHasBalance(account.getAccountNumber(), 0);

        double balance = SessionStorage.getSteps().getAccountById(account.getId()).getBalance();
        assertThat(balance).isEqualTo(0);
        assertThat(getAccountById(account.getId()).getBalance()).isZero();
    }

    @Test
    @DisplayName("Депозит без выбора аккаунта")
    @UserSession(testType = TestType.UI)
    @ApiVersion(version = "with_database_with_fix")
    public void userCanNotDepositWithoutAccount() {
        double amount = getRandomDouble(0, MAX_DEPOSIT_AMOUNT);
        CreateAccountResponse account = SessionStorage.getSteps().createAccount();
        DepositMoney depositMoney = new DepositMoney().open();

        depositMoney.getAmountInput().enter(String.valueOf(amount));
        depositMoney.getDepositButton().click();

        depositMoney.checkAlertMessageAndAccept(PLEASE_SELECT_ACCOUNT.getMessage())
                .open().getAccountSelector()
                .checkAccountHasBalance(account.getAccountNumber(), 0);

        SessionStorage.getSteps().getAllAccounts().forEach(
                createdAccount -> assertThat(createdAccount.getBalance()).isEqualTo(0)
        );
        assertThat(getAccountById(account.getId()).getBalance()).isZero();
    }
}
