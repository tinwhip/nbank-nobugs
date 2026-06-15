package db.entity.mapper;

import db.entity.TransactionEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionRowMapper implements RowMapper<TransactionEntity> {

    public static final TransactionRowMapper INSTANCE = new TransactionRowMapper();

    private TransactionRowMapper() {}

    @Override
    public TransactionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TransactionEntity.builder()
                .id(rs.getLong("id"))
                .amount(rs.getDouble("amount"))
                .type(rs.getString("type"))
                .timestamp(rs.getString("timestamp"))
                .accountId(rs.getLong("account_id"))
                .relatedAccountId(rs.getLong("related_account_id"))
                .build();
    }

}
