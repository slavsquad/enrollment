package org.product.catalog.analyzer.enrollment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportRequest {

    @Valid
    @NotNull
    @JsonAlias("items")
    private List<ImportNode> nodes;

    @NotNull
    private Date updateDate;
}
