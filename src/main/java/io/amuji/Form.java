package io.amuji;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Form {
    private String formId;
    private String formName;
    private String formNameCN;
    private String formNameTC;
    private String category;
    private String categoryId;
}
