package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportRequest;
import org.product.catalog.analyzer.enrollment.dto.Item;
import org.product.catalog.analyzer.enrollment.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${urls.app.imports.root}")
@Api(value = "API добавления записей", protocols = "http,https")
public class ItemController {

    private final ItemService itemService;

    @ApiOperation(value = "Получение записей из каталога")
    @GetMapping
    public ResponseEntity<Boolean> getItem() {
        log.info("GET ITEM METHOD");
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @ApiOperation(value = "Размещение записей в каталоге")
    @PostMapping
    public void addItem(@Valid @RequestBody ImportRequest importRequest) {

        //isValid(importDTO.getItems());

        for (Item item : importRequest.getItems()) {
            log.info("REST controller add: {}", item);
            item.setUpdateDate(importRequest.getUpdateDate());
            itemService.addItem(item);
        }
    }
    private void isValid(Item[] items) {

    }
}
