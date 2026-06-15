package db.entity.mapper;

import db.entity.AccountEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<AccountEntity> {

    public static final AccountRowMapper INSTANCE = new AccountRowMapper();

    private AccountRowMapper() {}

    @Override
    public AccountEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AccountEntity.builder()
                .id(rs.getLong("id"))
                .accountNumber(rs.getString("account_number"))
                .balance(rs.getDouble("balance"))
                .customerId(rs.getLong("customer_id"))
                .build();
    }

}
