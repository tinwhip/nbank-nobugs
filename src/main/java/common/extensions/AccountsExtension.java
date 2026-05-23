package common.extensions;

import common.annotations.AccountAnnotation;
import common.annotations.Accounts;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AccountsExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Accounts annotation = context.getRequiredTestMethod().getAnnotation(Accounts.class);

        if (annotation != null) {
            for (AccountAnnotation accountAnnotation : annotation.value()) {
                SessionStorage.addAccount(
                        accountAnnotation.accountName(),
                        SessionStorage.getSteps(accountAnnotation.user()).createAccount()
                );
            }
        }
    }
}
