package org.product.catalog.analyzer.enrollment.service;

import org.product.catalog.analyzer.enrollment.dto.ImportNode;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NodeService {
    void addItem(ImportNode node);
    void importNodes(List<ImportNode> nodes, Date updateDate);
    Set<UUID> findCategoryAllId();
}
