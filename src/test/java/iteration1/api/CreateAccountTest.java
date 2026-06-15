package iteration1.api;

import api.models.CreateAccountResponse;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import common.TestType;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import db.entity.AccountEntity;
import db.entity.CustomerEntity;
import db.entity.comparison.DaoAndModelAssertions;
import db.request.DbRequest;
import db.request.DbTable;
import db.request.RequestType;
import org.junit.jupiter.api.Test;
import api.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static db.request.Condition.equalTo;
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

        AccountEntity accountEntity = DbRequest.builder()
                .requestType(RequestType.SELECT)
                .table(DbTable.ACCOUNTS)
                .where(equalTo("id", accountResponse.getId()))
                .perform();

        DaoAndModelAssertions.assertThat(accountResponse, accountEntity).match();
    }
}
