package db.request;

import db.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static db.Databases.dataSource;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbRequest {
    private RequestType requestType;
    private List<Condition> conditions;
    private DbTable table;

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> List<T> performList() {
        Object[] args = conditions.stream()
                .map(Condition::getValue)
                .toArray();

        return (List<T>) new JdbcTemplate(dataSource()).query(buildSql(), table.getRowMapper(), args
        );
    }

    private String buildSql() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT -> sql.append("SELECT * FROM %s\n".formatted(table.name()));
            case UPDATE -> sql.append("UPDATE %s\n".formatted(table.name()));
            case DELETE -> sql.append("DELETE FROM %s\n".formatted(table.name()));
            case INSERT -> sql.append("INSERT INTO %s\n".formatted(table.name()));
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ");
            for (int i = 0; i < conditions.size(); i++) {
                if (i > 0) sql.append("\nAND ");
                Condition condition = conditions.get(i);

                sql.append(condition.getColumn()).append(" ")
                        .append(condition.getOperator()).append(" ? ");
            }
        }
        return sql.toString();
    }

    public static DbRequestBuilder builder() {
        return new DbRequestBuilder();
    }

    public static class DbRequestBuilder {
        private RequestType requestType;
        private List<Condition> conditions = new ArrayList<>();
        private DbTable table;

        public DbRequestBuilder request(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DbRequestBuilder from(DbTable table) {
            this.table = table;
            return this;
        }

        public DbRequestBuilder into(DbTable table) {
            this.table = table;
            return this;
        }

        public DbRequestBuilder where(Condition... conditions) {
            this.conditions.addAll(Arrays.asList(conditions));
            return this;
        }

        public <T extends BaseEntity> List<T> performList() {
            return DbRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .build()
                    .performList();
        }

        public <T extends BaseEntity> T perform() {
            List<T> result = performList();
            if (result.isEmpty()) {
                return null;
            }

            if (result.size() > 1) {
                throw new IllegalStateException("Expected one result, but got " + result.size());
            }

            return result.get(0);
        }
    }
}