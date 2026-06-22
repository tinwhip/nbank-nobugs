package db.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldUpdate {
    private String column;
    private Object value;
    private String operator;

    public static FieldUpdate field(String column, Object value) {
        return new FieldUpdate(column, value, "=");
    }
}
