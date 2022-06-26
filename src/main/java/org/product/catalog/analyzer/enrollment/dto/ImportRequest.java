package org.product.catalog.analyzer.enrollment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Класс, реализующий сущность, которая инкапсулирует данные,
 * поступающие на вход при импорте элементов.
 *
 * @author Stepanenko Stanislav
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportRequest {

    @Valid
    @NotNull
    private List<Node> items;

    @NotNull
    private Date updateDate;
}
