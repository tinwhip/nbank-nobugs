package db.dao;

import db.entity.AccountEntity;
import db.entity.CustomerEntity;

import java.util.Optional;

public interface CustomerDao {

    Optional<CustomerEntity> findCustomerById(Long id);

}
