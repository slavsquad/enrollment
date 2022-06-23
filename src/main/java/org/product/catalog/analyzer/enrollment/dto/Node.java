package org.product.catalog.analyzer.enrollment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.product.catalog.analyzer.enrollment.dto.NodeType.CATEGORY;
import static org.product.catalog.analyzer.enrollment.dto.NodeType.OFFER;

/**
 * Класс, реализующий сущность,
 * которая инкапсулирует данные узла(категория/товар) каталога.
 *
 * @author Stepanenko Stanislav
 */
@Data
@AllArgsConstructor
public class Node {

    @NotNull
    private  UUID id;

    @NotNull
    @Pattern(regexp = "^" + CATEGORY + "$|^" + OFFER + "$")
    private String type;

    @NotNull
    private String name;

    private UUID parentId;

    private Integer price;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date date;

    private List<Node> children;

    @JsonIgnore
    private int offerCount;
    @JsonIgnore
    private int sum;
}
