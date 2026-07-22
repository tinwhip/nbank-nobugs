package iteration1.api;

import api.models.CreateAccountResponse;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import db.entity.comparison.DaoAndModelAssertions;
import org.junit.jupiter.api.Test;
import api.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static db.steps.AccountsTableSteps.getAccountById;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseTest {

    @Test
    @UserSession(testType = TestType.API)
    public void userCanCreateAccountTest() {
        CreateAccountResponse accountResponse = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(SessionStorage.getUser()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);

        //запросить все аккаунты пользователя и проверить, что наш аккаунт там
        List<CreateAccountResponse> accounts = new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(SessionStorage.getUser()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).getAll(CreateAccountResponse[].class);
        assertThat(accountResponse).isEqualTo(accounts.get(0));
        DaoAndModelAssertions.assertThat(accountResponse, getAccountById(accountResponse.getId())).match();
    }
}
