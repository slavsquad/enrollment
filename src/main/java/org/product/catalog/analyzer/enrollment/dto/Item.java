package org.product.catalog.analyzer.enrollment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.UUID;

import static org.product.catalog.analyzer.enrollment.dto.NodeType.CATEGORY;
import static org.product.catalog.analyzer.enrollment.dto.NodeType.OFFER;

public record Item (
    @NotNull
    UUID id,

    @NotNull
    @Pattern(regexp = "^" + CATEGORY + "$|^" + OFFER + "$")
    String type,

    @NotNull
    String name,

    UUID parentId,

    Integer price,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    Date date
    ){}
