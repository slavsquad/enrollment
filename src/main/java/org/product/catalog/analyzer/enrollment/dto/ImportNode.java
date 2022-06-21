package org.product.catalog.analyzer.enrollment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

@Data
public class ImportNode {

    @NotNull
    private  UUID id;

    @NotNull
    @Pattern(regexp = "^OFFER$|^CATEGORY$")
    private String type;

    @NotNull
    private String name;

    private UUID parentId;

    private Integer price;

    private Date updateDate;
}
