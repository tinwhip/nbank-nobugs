package db.steps;

import db.entity.CustomerEntity;
import db.request.DbRequest;
import db.request.DbTable;
import db.request.RequestType;

import static db.request.Condition.equalTo;

public class CustomerTableSteps {

    public static CustomerEntity getUserByUsername(String username) {
        return DbRequest.builder()
                .requestType(RequestType.SELECT)
                .table(DbTable.CUSTOMERS)
                .where(equalTo("username", username))
                .perform();
    }
}
