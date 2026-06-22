package db.steps;

import db.entity.AccountEntity;
import db.request.DbRequest;
import db.request.DbTable;
import db.request.RequestType;

import static db.request.Condition.equalTo;
import static db.request.FieldUpdate.field;

public class AccountsTableSteps {

    public static AccountEntity getAccountById(long id) {
        return DbRequest.builder()
                .requestType(RequestType.SELECT)
                .table(DbTable.ACCOUNTS)
                .where(equalTo("id", id))
                .perform();
    }

    public static void updateAccountAmount(long accountId, double amount) {
        DbRequest.builder()
                .requestType(RequestType.UPDATE)
                .table(DbTable.ACCOUNTS)
                .set(field("balance", amount))
                .where(equalTo("id", accountId))
                .performUpdate();
    }

}