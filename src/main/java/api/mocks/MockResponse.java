package api.mocks;

import api.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class MockResponse<M extends BaseModel> {
    private Integer httpStatus;
    private Map<String, String> headers;
    private M body;
}
