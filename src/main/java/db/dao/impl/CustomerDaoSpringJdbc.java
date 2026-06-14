package db.dao.impl;

import db.dao.CustomerDao;
import db.entity.CustomerEntity;
import db.mapper.CustomerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static db.Databases.dataSource;

public class CustomerDaoSpringJdbc implements CustomerDao {

    @Override
    public Optional<CustomerEntity> findCustomerById(Long id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM customers WHERE id = ?",
                        CustomerRowMapper.INSTANCE,
                        id
                )
        );
    }

}
