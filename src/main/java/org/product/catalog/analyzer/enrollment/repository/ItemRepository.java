package org.product.catalog.analyzer.enrollment.repository;

import org.product.catalog.analyzer.enrollment.dto.Item;

import java.util.Set;
import java.util.UUID;

public interface ItemRepository {
    int save(Item item);
    Set<UUID> findCategoryAllId();
}
