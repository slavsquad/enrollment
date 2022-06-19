package org.product.catalog.analyzer.enrollment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @NotNull
    private String id;

    @NotNull
    @Pattern(regexp = "^OFFER$|^CATEGORY$")
    private String type;

    @NotNull
    private String name;

    private String parentId;

    Integer price;

    private Date date;
}
