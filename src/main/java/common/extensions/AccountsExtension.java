package common.extensions;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AccountsExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
//        UserAccounts annotation = context.getRequiredTestMethod().getAnnotation(UserAccounts.class);
//
//        if (annotation != null) {
//            for (AccountAnnotation accountAnnotation : annotation.value()) {
//                SessionStorage.addAccount(
//                        accountAnnotation.accountName(),
//                        SessionStorage.getSteps(accountAnnotation.user()).createAccount()
//                );
//            }
//        }
    }
}
