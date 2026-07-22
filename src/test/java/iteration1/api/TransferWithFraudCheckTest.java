package iteration1.api;

import api.mocks.MockEndpoint;
import api.models.CreateAccountResponse;
import api.models.TransferResponse;
import api.models.comparison.ModelAssertions;
import api.models.mockmodel.FraudCheckResponse;
import common.TestType;
import common.annotations.UserAccount;
import common.annotations.UserAccounts;
import common.annotations.UserSession;
import common.annotations.mock.FraudMockBody;
import common.annotations.mock.Mock;
import common.storage.SessionStorage;
import constants.FraudMessage;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import static api.models.mockmodel.FraudCheckResponse.buildExpectedTransferResponse;
import static api.requests.steps.UserSteps.MAX_TRANSFER_AMOUNT;

public class TransferWithFraudCheckTest extends BaseTest {

    @Test
    @Mock(endpoint = MockEndpoint.FRAUD_CHECK)
    @FraudMockBody()
    @UserSession(testType = TestType.API, value = 2)
    @UserAccounts({
            @UserAccount(name = "account1", user = 1, amount = MAX_TRANSFER_AMOUNT),
            @UserAccount(name = "account2", user = 2)
    })
    public void testTransferWithFraudCheckApproved(FraudCheckResponse mockResponse) {
        CreateAccountResponse account1 = SessionStorage.getAccount("account1");
        CreateAccountResponse account2 = SessionStorage.getAccount("account2");

        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        TransferResponse transferResponse = SessionStorage.getSteps(1).transferWithFraudCheck(
                account1.getId(),
                account2.getId(),
                transferAmount
        );

        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = buildExpectedTransferResponse(
                mockResponse,
                FraudMessage.SUCCESS,
                transferAmount,
                account1.getId(),
                account2.getId()
        );

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @Mock(endpoint = MockEndpoint.FRAUD_CHECK)
    @FraudMockBody(
            status = "MANUAL_REVIEW_REQUIRED",
            requiresManualReview = true
    )
    @UserSession(testType = TestType.API, value = 2)
    @UserAccounts({
            @UserAccount(name = "account1", user = 1, amount = MAX_TRANSFER_AMOUNT),
            @UserAccount(name = "account2", user = 2)
    })
    public void testTransferWithFraudCheckManualReviewRequired(FraudCheckResponse mockResponse) {
        CreateAccountResponse account1 = SessionStorage.getAccount("account1");
        CreateAccountResponse account2 = SessionStorage.getAccount("account2");

        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        TransferResponse transferResponse = SessionStorage.getSteps(1).transferWithFraudCheck(
                account1.getId(),
                account2.getId(),
                transferAmount
        );

        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = buildExpectedTransferResponse(
                mockResponse,
                FraudMessage.MANUAL_REVIEW_REQUIRED,
                transferAmount,
                account1.getId(),
                account2.getId()
        );

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @Mock(endpoint = MockEndpoint.FRAUD_CHECK)
    @FraudMockBody(
            status = "VERIFICATION_REQUIRED",
            additionalVerificationRequired = true
    )
    @UserSession(testType = TestType.API, value = 2)
    @UserAccounts({
            @UserAccount(name = "account1", user = 1, amount = MAX_TRANSFER_AMOUNT),
            @UserAccount(name = "account2", user = 2)
    })
    public void testTransferWithFraudCheckVerificationRequired(FraudCheckResponse mockResponse) {
        CreateAccountResponse account1 = SessionStorage.getAccount("account1");
        CreateAccountResponse account2 = SessionStorage.getAccount("account2");

        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        TransferResponse transferResponse = SessionStorage.getSteps(1).transferWithFraudCheck(
                account1.getId(),
                account2.getId(),
                transferAmount
        );

        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = buildExpectedTransferResponse(
                mockResponse,
                FraudMessage.VERIFICATION_REQUIRED,
                transferAmount,
                account1.getId(),
                account2.getId()
        );

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @Mock(endpoint = MockEndpoint.FRAUD_CHECK)
    @FraudMockBody(
            reason = "High risk transaction!",
            riskScore = 2
    )
    @UserSession(testType = TestType.API, value = 2)
    @UserAccounts({
            @UserAccount(name = "account1", user = 1, amount = MAX_TRANSFER_AMOUNT),
            @UserAccount(name = "account2", user = 2)
    })
    public void testTransferWithFraudOtherReason(FraudCheckResponse mockResponse) {
        CreateAccountResponse account1 = SessionStorage.getAccount("account1");
        CreateAccountResponse account2 = SessionStorage.getAccount("account2");

        double transferAmount = RandomUtils.nextDouble(1, MAX_TRANSFER_AMOUNT);
        TransferResponse transferResponse = SessionStorage.getSteps(1).transferWithFraudCheck(
                account1.getId(),
                account2.getId(),
                transferAmount
        );

        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = buildExpectedTransferResponse(
                mockResponse,
                FraudMessage.SUCCESS,
                transferAmount,
                account1.getId(),
                account2.getId()
        );

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}
