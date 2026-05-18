package common.extensions;

import api.models.CreateUserRequest;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        //Шаг 1: проверка, есть ли у теста аннотация AdminSession
        AdminSession annotation = context.getRequiredTestMethod().getAnnotation(AdminSession.class);

        if (annotation != null) { //Шаг 2: если есть, добавляем в локал сторадж токен админа
            BasePage.authAsUser(CreateUserRequest.getAdmin());
        }
    }
}
