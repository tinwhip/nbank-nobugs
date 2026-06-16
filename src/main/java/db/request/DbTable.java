package db.request;

import db.entity.mapper.AccountRowMapper;
import db.entity.mapper.CustomerRowMapper;
import db.entity.mapper.TransactionRowMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.jdbc.core.RowMapper;

@AllArgsConstructor
@Getter
public enum DbTable {
    CUSTOMERS(CustomerRowMapper.INSTANCE),
    ACCOUNTS(AccountRowMapper.INSTANCE),
    TRANSACTIONS(TransactionRowMapper.INSTANCE);

    private final RowMapper<?> rowMapper;
}
