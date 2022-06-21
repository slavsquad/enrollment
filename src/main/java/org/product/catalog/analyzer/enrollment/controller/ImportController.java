package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportRequest;
import org.product.catalog.analyzer.enrollment.dto.Item;
import org.product.catalog.analyzer.enrollment.service.ItemService;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${urls.imports}")
@Api(value = "API импортирования записей", protocols = "http,https")
public class ImportController {

    private final ItemService itemService;

    @ApiOperation(value = "Размещение записей в каталоге")
    @PostMapping
    public void addItem(@Valid @RequestBody ImportRequest importRequest) throws Exception {
        final List<Item> items = importRequest.getItems();
        isValidImportItems(items);
        for (Item item : items) {
            item.setUpdateDate(importRequest.getUpdateDate());
            itemService.addItem(item);
        }
    }

    private void isValidImportItems(List<Item> items) throws Exception {

        final Set<UUID> idSet = new HashSet<>();

        final Set<UUID> categoryIdSet = itemService.findCategoryAllId();
        categoryIdSet.addAll(items
                .stream()
                .filter(item -> "CATEGORY".equals(item.getType()))
                .map(Item::getId)
                .collect(Collectors.toSet()));

        for (Item item : items) {
            idSet.add(item.getId());
            if ("CATEGORY".equals(item.getType()) && item.getPrice() != null) {
                throw new ArgumentNotValidException("Category price must be null!");
            }
            if ("OFFER".equals(item.getType()) && (item.getPrice() == null || item.getPrice() < 0)) {
                throw new ArgumentNotValidException("Offer price must not be null or be positive number!");
            }
            if (item.getParentId() != null && !categoryIdSet.contains(item.getParentId())) {
                throw new ArgumentNotValidException("Parent ID: " + item.getParentId() + " is not a category!");
            }
        }

        if (idSet.size() != items.size()) {
            throw new ArgumentNotValidException("Import items contains duplicates id!");
        }
    }
}
