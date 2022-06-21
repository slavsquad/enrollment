package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportRequest;
import org.product.catalog.analyzer.enrollment.dto.ImportNode;
import org.product.catalog.analyzer.enrollment.service.NodeService;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@Api(value = "API импортирования записей", protocols = "http,https")
public class NodeController {

    private final NodeService itemService;

    @PostMapping("${urls.imports}")
    @ApiOperation(value = "Размещение записей в каталоге")
    public void importNodes(@Valid @RequestBody ImportRequest importRequest) throws Exception {
        final List<ImportNode> nodes = importRequest.getNodes();
        validateImportNodes(nodes);
        itemService.importNodes(nodes, importRequest.getUpdateDate());
    }

    @GetMapping("${urls.nodes}")
    @ApiOperation(value = "Получение записей из каталога")
    public ResponseEntity<Boolean> getNode() {
        log.info("GET ITEM METHOD");
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private void validateImportNodes(List<ImportNode> nodes) throws Exception {

        final Set<UUID> idSet = new HashSet<>();

        final Set<UUID> categoryIdSet = itemService.findCategoryAllId();
        categoryIdSet.addAll(nodes
                .stream()
                .filter(node -> "CATEGORY".equals(node.getType()))
                .map(ImportNode::getId)
                .collect(Collectors.toSet()));

        for (ImportNode node : nodes) {
            idSet.add(node.getId());
            if ("CATEGORY".equals(node.getType()) && node.getPrice() != null) {
                throw new ArgumentNotValidException("Category price must be null!");
            }
            if ("OFFER".equals(node.getType()) && (node.getPrice() == null || node.getPrice() < 0)) {
                throw new ArgumentNotValidException("Offer price must not be null or be positive number!");
            }
            if (node.getParentId() != null && !categoryIdSet.contains(node.getParentId())) {
                throw new ArgumentNotValidException("Parent ID: " + node.getParentId() + " is not a category!");
            }
        }

        if (idSet.size() != nodes.size()) {
            throw new ArgumentNotValidException("Import records contains duplicates id!");
        }
    }
}
