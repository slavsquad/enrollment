package org.product.catalog.analyzer.enrollment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @NotNull
    private  UUID id;

    @NotNull
    @Pattern(regexp = "^OFFER$|^CATEGORY$")
    private String type;

    @NotNull
    private String name;

    private UUID parentId;

    Integer price;

    private Date updateDate;
}
