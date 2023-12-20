package io.amuji;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Request {
    private String formId;
    private String formName;
    private String formNameCN;
    private String category;
}
