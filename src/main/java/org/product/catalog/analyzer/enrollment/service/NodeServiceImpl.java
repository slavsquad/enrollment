package org.product.catalog.analyzer.enrollment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.dto.NodeType;
import org.product.catalog.analyzer.enrollment.repository.NodeRepository;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.product.catalog.analyzer.enrollment.validation.exception.NotFindNodeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса описывающего сервисные методы вставки, поиска и удаления узлов.
 *
 * @author Stepanenko Stanislav
 */
@Slf4j
@Service
@AllArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;

    /**
     * Реализация метода поиска узла в полную глубину по идентификатору.
     * Метод возвращает узел со всеми потомками, полностью отображая
     * структуру каталога товаров.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел со всеми потомками, или {@code null} если узел не найден.
     * @throws NotFindNodeException если элемент не найден.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Node findById(UUID id) throws NotFindNodeException {
        final Node result = nodeRepository.findDepthNodeById(id);
        if (result == null) throw new NotFindNodeException("Node with id: " + id + " didn't find!");
        log.info("Looking for node by id: {}", id);
        return result;
    }

    /**
     * Реализация метода импортирования узлов(товаров/категории) в каталоге товаров,
     * путем добавленные новых либо обновление текущих.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param nodes      - список узлов, который необходимо добавить в каталог.
     * @param updateDate - дата импорта узлов в каталог.
     * @throws ArgumentNotValidException если какой либо из узел не прошел проверку.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importNodes(List<Node> nodes, Date updateDate) throws ArgumentNotValidException {
        validateImportNodes(nodes);
        nodes.forEach(node -> node.setDate(updateDate));
        log.info("{} nodes are ready to import!", nodes.size());
        nodeRepository.saveAll(nodes);
    }

    /**
     * Приватный метод реализующий ряд проверок перед запуском импорта узлов.
     *
     * @param nodes - список узлов которые необходимо проверить.
     * @throws ArgumentNotValidException если какой либо из узел не прошел проверку.
     */
    private void validateImportNodes(List<Node> nodes) throws ArgumentNotValidException {
        log.info("Start validation: {} nodes for import.", nodes.size());

        if (nodes.size() != nodes.stream().map(Node::getId).collect(Collectors.toSet()).size()) {
            throw new ArgumentNotValidException("Import records contains duplicates id!");
        }

        final Set<UUID> importCategorySet = nodes
                .stream()
                .filter(node -> NodeType.CATEGORY.equals(node.getType()))
                .map(Node::getId)
                .collect(Collectors.toSet());


        for (Node importNode : nodes) {

            if (NodeType.CATEGORY.equals(importNode.getType()) && importNode.getPrice() != null) {
                throw new ArgumentNotValidException("Category price must be null!");
            }

            if (NodeType.OFFER.equals(importNode.getType()) && (importNode.getPrice() == null || importNode.getPrice() < 0)) {
                throw new ArgumentNotValidException("Offer price must not be null or be positive number!");
            }

            UUID oldParentId = null;
            final Node oldNode = nodeRepository.findPlainNodeById(importNode.getId());
            if (oldNode != null) {
                oldParentId = oldNode.getOldParentId();
                if (!importNode.getType().equals(oldNode.getType()))
                    throw new ArgumentNotValidException("Change node type is not allowed!");
            }

            if (importNode.getParentId() != null && !importCategorySet.contains(importNode.getParentId())) {
                final Node parentCategory = nodeRepository.findPlainNodeById(importNode.getParentId());
                if (parentCategory == null || NodeType.OFFER.equals(parentCategory.getType())) {
                    throw new ArgumentNotValidException("Category with ID: " + importNode.getParentId() + " is didn't find or node is not a category!");
                }
                oldParentId = parentCategory.getId();
                importCategorySet.add(parentCategory.getId());
            }
            importNode.setOldParentId(oldParentId);
        }

        log.info("Success validation: {} nodes for import.", nodes.size());
    }

    /**
     * Реализация сервисного метода удаления узла по идентификатору.
     * Метод удаляет узел со всеми потомками если таковые имеются.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов.
     * @throws NotFindNodeException если элемент не найден.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int deleteById(UUID id) throws NotFindNodeException {
        log.info("Deleting a node by id: {}", id);
        int result = 0;

        final Node node = nodeRepository.findPlainNodeById(id);
        if (node == null) {
            throw new NotFindNodeException("Node with id: " + id + " didn't find!");
        }

        if (NodeType.CATEGORY.equals(node.getType())) {
            result += nodeRepository.deleteAllDescendantById(id);
        }

        result += nodeRepository.deleteNodeById(id);
        return result;
    }
}
