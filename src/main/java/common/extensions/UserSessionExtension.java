package common.extensions;

import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import common.TestType;
import common.annotations.UserAccount;
import common.annotations.UserAccounts;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import ui.pages.BasePage;

import java.util.LinkedList;
import java.util.List;

import static db.steps.AccountsTableSteps.updateAccountAmount;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        //Шаг 1: проверка, что у теста есть аннотация UserSession
        UserSession annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            int userCount = annotation.value();

            SessionStorage.clear();

            List<CreateUserRequest> users = new LinkedList<>();

            for (int i = 0; i < userCount; i++) {
                CreateUserRequest user = AdminSteps.createUser();
                users.add(user);
            }

            SessionStorage.addUsers(users);

            int authAsUser = annotation.auth();

            UserAccounts accounts = context.getRequiredTestMethod().getAnnotation(UserAccounts.class);
            if (accounts != null) {
                createAccounts(accounts);
            }

            if (annotation.testType() == TestType.UI) {
                BasePage.authAsUser(SessionStorage.getUser(authAsUser));
            }
        }
    }

    private void createAccounts(UserAccounts annotation) {
        for (UserAccount userAccount : annotation.value()) {
            String accountName = userAccount.name();
            int userNumber = userAccount.user();

            CreateAccountResponse account = SessionStorage.getSteps(userNumber).createAccount();

            SessionStorage.addAccount(accountName, account);

            if (userAccount.amount() > 0) {
                updateAccountAmount(account.getId(), userAccount.amount());
            }
        }
    }
}
