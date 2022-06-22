package org.product.catalog.analyzer.enrollment.service;

import org.product.catalog.analyzer.enrollment.dto.Node;

import java.util.*;

public interface NodeService {
    void addNode(Node node);

    Node findById(UUID id);

    void importNodes(List<Node> nodes, Date updateDate);

    Set<UUID> findCategoryAllId();
}
