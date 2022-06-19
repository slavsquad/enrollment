package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportRequest;
import org.product.catalog.analyzer.enrollment.dto.Item;
import org.product.catalog.analyzer.enrollment.service.ItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${urls.app.imports.root}")
@Api(value = "API добавления записей", protocols = "http,https")
public class ItemController {

    private final ItemService itemService;

    @ApiOperation(value = "Размещение записей в каталоге")
    @PostMapping
    public void addItem(@Valid @RequestBody ImportRequest importRequest) {

        //isValid(importDTO.getItems());

        for (Item item : importRequest.getItems()) {
            log.info("REST controller add: {}", item);
            item.setDate(importRequest.getUpdateDate());
            itemService.addItem(item);
        }
    }

    private void isValid(Item[] items) {

    }
}
