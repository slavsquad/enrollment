package org.product.catalog.analyzer.enrollment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
     * Реализация метода вставки узла(товара/категории) в каталоге товаров,
     * путем добавленные новой позиций либо обновление текущей.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param node - узел, который необходимо добавить в каталог.
     */
    @Override
    public void addNode(Node node) {
        log.info("Node with id:{} is ready to import!", node.getId());
        nodeRepository.save(node);
    }

    /**
     * Реализация метода поиска узла в полную глубину по идентификатору.
     * Метод возвращает узел со всеми потомками, полностью отображая
     * структуру каталога товаров.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел со всеми потомками, или {@code null} если узел не найден.
     */
    @Override
    public Node findById(UUID id) {
        log.info("Looking for node by id: {}", id);
        return nodeRepository.findById(id);
    }

    /**
     * Реализация метода импортирования узлов(товаров/категории) в каталоге товаров,
     * путем добавленные новых либо обновление текущих.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param nodes - список узлов, который необходимо добавить в каталог.
     * @param updateDate - дата импорта узлов в каталог.
     */
    @Override
    public void importNodes(List<Node> nodes, Date updateDate) {
        for (Node item : nodes) {
            item.setDate(updateDate);
        }
        log.info("{} nodes are ready to import!", nodes.size());
        nodeRepository.saveAll(nodes);
    }

    /**
     * Реализация поиска идентификаторов имеющихся в каталоге категорий товаров.
     *
     * @return список идентификаторов категорий присутствующий в каталоге.
     */
    @Override
    public Set<UUID> findCategoryAllId() {
        final Set<UUID> categoryIdSet = nodeRepository.findCategoryAllId();
        log.info("Find {} id category from repository!", categoryIdSet.size());
        return categoryIdSet;
    }

    /**
     * Реализация сервисного метода удаления узла по идентификатору.
     * Метод удаляет узел со всеми потомками если таковые имеются.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов.
     */
    @Override
    public int deleteById(UUID id) {
        log.info("Deleting a node by id: {}", id);
        return nodeRepository.deleteById(id);
    }
}
