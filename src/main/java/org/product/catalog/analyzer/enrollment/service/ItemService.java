package org.product.catalog.analyzer.enrollment.service;

import org.product.catalog.analyzer.enrollment.dto.Item;

import java.util.Set;
import java.util.UUID;

public interface ItemService {
    void addItem(Item item);
    Set<UUID> findCategoryAllId();
}
