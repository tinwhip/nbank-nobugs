package iteration1.ui;

import api.models.CreateAccountResponse;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import db.entity.comparison.DaoAndModelAssertions;
import org.junit.jupiter.api.Test;
import constants.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static db.steps.AccountsTableSteps.getAccountById;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {

    @Test
    @UserSession(testType = TestType.UI)
    public void userCanCreateAccountTest() {
        new UserDashboard().open().createNewAccount();

        List<CreateAccountResponse> createdAccounts = SessionStorage.getSteps().getAllAccounts();

        assertThat(createdAccounts).hasSize(1);

        new UserDashboard().checkAlertMessageAndAccept
                (BankAlert.NEW_ACCOUNT_CREATED.getMessage() + createdAccounts.get(0).getAccountNumber());

        assertThat(createdAccounts.get(0).getBalance()).isZero();
        DaoAndModelAssertions.assertThat(createdAccounts.get(0), getAccountById(createdAccounts.get(0).getId())).match();
    }
}
