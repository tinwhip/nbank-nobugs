package db.steps;

import constants.TransferTypes;
import db.entity.CustomerEntity;
import db.entity.TransactionEntity;
import db.request.DbRequest;
import db.request.DbTable;
import db.request.RequestType;

import static constants.TransferTypes.TRANSFER_IN;
import static constants.TransferTypes.TRANSFER_OUT;
import static db.request.Condition.equalTo;

public class TransactionsTableSteps {

    public static TransactionEntity getSenderTransaction(long senderAccountId, long receiverAccountId) {
        return DbRequest.builder()
                .request(RequestType.SELECT)
                .from(DbTable.TRANSACTIONS)
                .where(
                        equalTo("type", TRANSFER_OUT.name()),
                        equalTo("account_id", senderAccountId),
                        equalTo("related_account_id", receiverAccountId)
                )
                .perform();
    }

    public static TransactionEntity getReceiverTransaction(long receiverAccountId, long senderAccountId) {
        return DbRequest.builder()
                .request(RequestType.SELECT)
                .from(DbTable.TRANSACTIONS)
                .where(
                        equalTo("type", TRANSFER_IN.name()),
                        equalTo("account_id", receiverAccountId),
                        equalTo("related_account_id", senderAccountId)
                )
                .perform();
    }

}
