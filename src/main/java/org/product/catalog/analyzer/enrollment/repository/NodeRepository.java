package org.product.catalog.analyzer.enrollment.repository;

import org.product.catalog.analyzer.enrollment.dto.Node;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface NodeRepository {
    Optional<Node> findById(UUID id);
    int save(Node node);
    int saveAll(List<Node> nodes);
    Set<UUID> findCategoryAllId();
}
