package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Item;
import org.product.catalog.analyzer.enrollment.dto.SalesResponse;
import org.product.catalog.analyzer.enrollment.service.NodeService;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Класс, реализующий REST-контролер, который отвечает за обработку запросов к приложению
 * связанными с основными операциями над узлами(продуктами/категориями).
 *
 * @author Stepanenko Stanislav
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Api(tags = "Дополнительные задачи", protocols = "http,https")
public class ExtraController {

    private final NodeService nodeService;

    /**
     * Метод обрабатывает Get-запрос Получение списка товаров,
     * цена которых была обновлена за последние 24 часа включительно [now() - 24h, now()] от времени переданном в запросе.
     *
     * @param date - указанная дата.
     * @throws ArgumentNotValidException если какой либо из аргументов запроса не прошёл проверку.
     */
    @GetMapping("${urls.sales}")
    @ApiOperation(value = "- получение списка товаров", notes = """
            Получение списка товаров, цена которых была обновлена за последние 24 часа включительно [now() - 24h, now()] от времени переданном в запросе. Обновление цены не означает её изменение. Обновления цен удаленных товаров недоступны. При обновлении цены товара, средняя цена категории, которая содержит этот товар, тоже обновляется.
            """)
    public SalesResponse salesNode(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date
    ) {
        log.info("Received request to find sale date: {} ", date);
        return new SalesResponse(nodeService.findSaleList(date).stream().map(node -> new Item(
                node.getId(),
                node.getType(),
                node.getName(),
                node.getParentId(),
                node.getPrice(),
                node.getDate()
        )).collect(Collectors.toList()));
    }
}
