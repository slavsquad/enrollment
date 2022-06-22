package org.product.catalog.analyzer.enrollment.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportRequest;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.dto.NodeType;
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

    private final NodeService nodeService;

    @PostMapping("${urls.imports}")
    @ApiOperation(value = "Размещение записей в каталоге")
    public void importNodes(@Valid @RequestBody ImportRequest importRequest) throws Exception {
        final List<Node> nodes = importRequest.getNodes();
        log.info("Received request to import nodes: {} update date: {}.", nodes.size(), importRequest.getUpdateDate());
        validateImportNodes(nodes);
        nodeService.importNodes(nodes, importRequest.getUpdateDate());
    }

    @GetMapping("${urls.nodes}")
    @ApiOperation(value = "Получение записей из каталога")
    public ResponseEntity<Node> getNode(@RequestParam UUID id) {
        log.info("Get request info for node by id: {}", id);
        final Node result = nodeService.findById(id);
        log.info("Find node with ID: {}", result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void validateImportNodes(List<Node> nodes) throws Exception {
        log.info("Start validation: {} nodes for import.", nodes.size());
        final Set<UUID> idSet = new HashSet<>();

        final Set<UUID> categoryIdSet = nodeService.findCategoryAllId();
        categoryIdSet.addAll(nodes
                .stream()
                .filter(node -> NodeType.CATEGORY.equals(node.getType()))
                .map(Node::getId)
                .collect(Collectors.toSet()));

        for (Node node : nodes) {
            idSet.add(node.getId());
            if (NodeType.CATEGORY.equals(node.getType()) && node.getPrice() != null) {
                throw new ArgumentNotValidException("Category price must be null!");
            }
            if (NodeType.OFFER.equals(node.getType()) && (node.getPrice() == null || node.getPrice() < 0)) {
                throw new ArgumentNotValidException("Offer price must not be null or be positive number!");
            }
            if (node.getParentId() != null && !categoryIdSet.contains(node.getParentId())) {
                throw new ArgumentNotValidException("Parent ID: " + node.getParentId() + " is not a category!");
            }
        }

        if (idSet.size() != nodes.size()) {
            throw new ArgumentNotValidException("Import records contains duplicates id!");
        }
        log.info("Finish validation: {} nodes for import.", nodes.size());
    }
}
