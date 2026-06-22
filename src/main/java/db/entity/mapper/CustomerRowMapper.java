package db.entity.mapper;

import db.entity.CustomerEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<CustomerEntity> {

    public static final CustomerRowMapper INSTANCE = new CustomerRowMapper();

    private CustomerRowMapper() {}

    @Override
    public CustomerEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return CustomerEntity.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .name(rs.getString("name"))
                .role(rs.getString("role"))
                .build();
    }

}
