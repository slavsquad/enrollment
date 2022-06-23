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
@Api(tags = "Базовые задачи", protocols = "http,https")
public class NodeController {

    private final NodeService nodeService;

    /**
     * Метод обрабатывает POST-запрос на импортирование  узлов в каталог товаров.
     *
     * @param importRequest - запрос на пакетный импорт узлов.
     * @throws ArgumentNotValidException если какой либо из аргументов запроса не прошёл проверку.
     */
    @PostMapping("${urls.imports}")
    @ApiOperation(value = "- импортирует новые товары и/или категории", notes = """
            Товары/категории импортированные повторно обновляют текущие. Изменение типа элемента с товара на категорию или с категории на товар не допускается. Порядок элементов в запросе является произвольным.
                            
              - uuid товара или категории является уникальным среди товаров и категорий
              - родителем товара или категории может быть только категория
              - принадлежность к категории определяется полем parentId
              - товар или категория могут не иметь родителя (при обновлении parentId на null, элемент остается без родителя)
              - название элемента не может быть null
              - у категорий поле price должно содержать null
              - цена товара не может быть null и должна быть больше либо равна нулю.
              - при обновлении товара/категории обновленными считаются **все** их параметры
              - при обновлении параметров элемента обязательно обновляется поле **date** в соответствии с временем обновления
              - в одном запросе не может быть двух элементов с одинаковым id
              - дата должна обрабатываться согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, необходимо отвечать 400.
                            
            Гарантируется, что во входных данных нет циклических зависимостей и поле updateDate монотонно возрастает. Гарантируется, что при проверке передаваемое время кратно секундам.
            """)
    public void importNodes(@Valid @RequestBody ImportRequest importRequest) throws ArgumentNotValidException {
        final List<Node> nodes = importRequest.getNodes();
        log.info("Received request to import nodes: {} update date: {}.", nodes.size(), importRequest.getUpdateDate());
        validateImportNodes(nodes);
        nodeService.importNodes(nodes, importRequest.getUpdateDate());
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

    /**
     * Метод обрабатывает DELETE-запрос на удаление узла в полную глубину по идентификатору.
     * Метод удаляет узел со всеми потомками.
     *
     * @param id - идентификатор корневого узла(товара/категории)
     * @throws NotFindNodeException если элемент не найден.
     */
    @DeleteMapping("${urls.delete}")
    @ApiOperation(value = "- удаляет элемент по идентификатору", notes = """
            При удалении категории удаляются все дочерние элементы. Доступ к статистике (истории обновлений) удаленного элемента невозможен.
                        
            Так как время удаления не передается, при удалении элемента время обновления родителя изменять не нужно.
                        
            Обратите, пожалуйста, внимание на этот обработчик. При его некорректной работе тестирование может быть невозможно.
            """)
    public void deleteNode(@RequestParam UUID id) throws NotFindNodeException {
        log.info("Request for delete node by id: {}", id);
        final int result = nodeService.deleteById(id);
        if (result == 0) throw new NotFindNodeException("Node with id: " + id + " didn't find!");
        log.info("Delete node with ID: {}", id);
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
    @ApiOperation(value = "- предоставляет информацию об элементе по идентификатору", notes = """
            При получении информации о категории также предоставляется информация о её дочерних элементах.
                            
              - для пустой категории поле children равно пустому массиву, а для товара равно null
              - цена категории - это средняя цена всех её товаров, включая товары дочерних категорий. Если категория не содержит товаров цена равна null. При обновлении цены товара, средняя цена категории, которая содержит этот товар, тоже обновляется.              
                            
            """)
    public ResponseEntity<Node> getNode(@RequestParam UUID id) throws NotFindNodeException {
        log.info("Get request info for node by id: {}", id);
        final Node result = nodeService.findById(id);
        if (result == null) throw new NotFindNodeException("Node with id: " + id + " didn't find!");
        log.info("Find node with ID: {}", result.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
