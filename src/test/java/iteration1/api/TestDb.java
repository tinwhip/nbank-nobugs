package iteration1.api;

import db.CustomerDbClient;
import db.entity.CustomerEntity;
import org.junit.jupiter.api.Test;

public class TestDb {

    @Test
    public void getCustomerTest() {
        CustomerEntity customerById = new CustomerDbClient().getCustomerById(4L);
    }
}
