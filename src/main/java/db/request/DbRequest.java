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
import java.util.stream.Stream;

import static db.Databases.dataSource;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbRequest {
    private RequestType requestType;
    private List<Condition> conditions;
    private DbTable table;
    private List<FieldUpdate> fieldUpdate;

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> List<T> performList() {
        Object[] args = conditions.stream()
                .map(Condition::getValue)
                .toArray();

        return (List<T>) new JdbcTemplate(dataSource()).query(buildSql(), table.getRowMapper(), args);
    }

    private void performUpdate() {
        Object[] args = Stream.concat(
                fieldUpdate.stream().map(FieldUpdate::getValue),
                conditions.stream().map(Condition::getValue)
        ).toArray();

        new JdbcTemplate(dataSource()).update(buildSql(), args);
    }

    private String buildSql() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT -> sql.append("SELECT * FROM %s\n".formatted(table.name()));
            case UPDATE -> sql.append("UPDATE %s\n".formatted(table.name()));
            case DELETE -> sql.append("DELETE FROM %s\n".formatted(table.name()));
            case INSERT -> sql.append("INSERT INTO %s\n".formatted(table.name()));
        }

        if (fieldUpdate != null && !fieldUpdate.isEmpty()) {
            sql.append("SET ");
            for (int i = 0; i < fieldUpdate.size(); i++) {
                if (i > 0) {sql.append(", ");}
                FieldUpdate field = fieldUpdate.get(i);
                sql.append(field.getColumn()).append(" = ? ");
            }
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ");
            for (int i = 0; i < conditions.size(); i++) {
                if (i > 0) {sql.append("\nAND ");}
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
        private List<FieldUpdate> fieldsUpdate = new ArrayList<>();

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

        public DbRequestBuilder set(FieldUpdate... fieldsUpdate) {
            this.fieldsUpdate.addAll(Arrays.asList(fieldsUpdate));
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

        public void performUpdate() {
            if (requestType != RequestType.UPDATE) {
                throw new IllegalArgumentException("Method allowed only for UPDATE RequestType");
            }
            DbRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .fieldUpdate(fieldsUpdate)
                    .conditions(conditions)
                    .build()
                    .performUpdate();
        }
    }
}