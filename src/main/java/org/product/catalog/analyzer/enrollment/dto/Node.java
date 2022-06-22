package org.product.catalog.analyzer.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.product.catalog.analyzer.enrollment.dto.NodeType.CATEGORY;
import static org.product.catalog.analyzer.enrollment.dto.NodeType.OFFER;

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

    private Date date;

    private List<Node> children;

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", price=" + price +
                ", date=" + date +
                ", children=" + children +
                '}';
    }
}
