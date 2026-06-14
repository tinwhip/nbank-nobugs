package db;

import db.dao.CustomerDao;
import db.dao.impl.CustomerDaoJdbc;
import db.dao.impl.CustomerDaoSpringJdbc;
import db.entity.CustomerEntity;

public class CustomerDbClient {

    private final CustomerDao customerDao = new CustomerDaoSpringJdbc();

    public CustomerEntity getCustomerById(Long id) {
        return customerDao.findCustomerById(id)
                .orElseThrow(() -> new RuntimeException("Не получилось достать кастомера"));
    }
}
