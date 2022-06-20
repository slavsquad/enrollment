package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${urls.nodes}")
@Api(value = "API получения записи", protocols = "http,https")
public class NodeController {

    private final ItemService itemService;

    @GetMapping
    @ApiOperation(value = "Получение записей из каталога")
    public ResponseEntity<Boolean> getItem() {
        log.info("GET ITEM METHOD");
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
