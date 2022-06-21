package org.product.catalog.analyzer.enrollment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Item;
import org.product.catalog.analyzer.enrollment.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public void addItem(Item item) {
        if (itemRepository.save(item) != 1) throw new IllegalStateException("oops something went wrong");
        log.info("Import item : {}", item);
    }

    @Override
    public Set<UUID> findCategoryAllId() {
        final Set<UUID> categoryIdSet = itemRepository.findCategoryAllId();
        log.info("Find category all id : {}", categoryIdSet);
        return categoryIdSet;
    }
}
