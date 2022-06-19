package org.product.catalog.analyzer.enrollment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportRequest {

    @Valid
    @NotNull
    private Item[] items;

    @NotNull
    private Date updateDate;
}
