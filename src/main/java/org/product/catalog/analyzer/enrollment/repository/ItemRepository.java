package org.product.catalog.analyzer.enrollment.repository;

import org.product.catalog.analyzer.enrollment.dto.Item;

import java.util.Set;

public interface ItemRepository {
    int save(Item item);
    Set<String> findCategoryListId();
}
