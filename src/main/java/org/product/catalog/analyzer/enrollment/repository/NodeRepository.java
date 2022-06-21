package org.product.catalog.analyzer.enrollment.repository;

import org.product.catalog.analyzer.enrollment.dto.ImportNode;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NodeRepository {
    int save(ImportNode node);
    int saveAll(List<ImportNode> nodes);
    Set<UUID> findCategoryAllId();
}
