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
import org.product.catalog.analyzer.enrollment.validation.exception.NotFindNodeException;
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
@Api(value = "API импортирования записей", protocols = "http,https")
public class NodeController {

    private final NodeService nodeService;

    /**
     * Метод обрабатывает POST-запрос на импортирование  узлов в каталог товаров.
     *
     * @param importRequest - запрос на пакетный импорт узлов.
     * @throws ArgumentNotValidException если какой либо из аргументов запроса не прошёл проверку.
     */
    @PostMapping("${urls.imports}")
    @ApiOperation(value = "Размещение записей в каталоге")
    public void importNodes(@Valid @RequestBody ImportRequest importRequest) throws ArgumentNotValidException {
        final List<Node> nodes = importRequest.getNodes();
        log.info("Received request to import nodes: {} update date: {}.", nodes.size(), importRequest.getUpdateDate());
        validateImportNodes(nodes);
        nodeService.importNodes(nodes, importRequest.getUpdateDate());
    }

    /**
     * Метод обрабатывает GET-запрос на поиск узла в полную глубину по идентификатору.
     * Метод возвращает узел со всеми потомками, полностью отображая структуру каталога товаров.
     *
     * @param id - идентификатор корневого узла(товара/категории)
     * @return ответ в котором содержится узел в JSON формате со всеми потомками
     * @throws NotFindNodeException если элемент не найден.
     */
    @GetMapping("${urls.nodes}")
    @ApiOperation(value = "Получение записей из каталога")
    public ResponseEntity<Node> getNode(@RequestParam UUID id) throws NotFindNodeException {
        log.info("Get request info for node by id: {}", id);
        final Node result = nodeService.findById(id);
        if (result == null) throw new NotFindNodeException("Node with id: " + id + " didn't find!");
        log.info("Find node with ID: {}", result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Приватный метод реализующий ряд проверок перед запуском импорта узлов.
     *
     * @param nodes - список узлов которые необходимо проверить.
     * @throws ArgumentNotValidException если какой либо из узел не прошел проверку.
     */
    private void validateImportNodes(List<Node> nodes) throws ArgumentNotValidException {
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
