package org.product.catalog.analyzer.enrollment.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.dto.NodeType;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Реализация интерфейса описывающего методы взаимодействия приложения с базой данных.
 *
 * @author Stepanenko Stanislav
 */
@Slf4j
@Repository
@AllArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {
    final private static byte WHITE = 0;
    final private static byte GRAY = -1;
    final private static byte BLACK = 1;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Реализация метода поиска узла в полную глубину по идентификатору.
     * Метод возвращает узел со всеми потомками, полностью отображая
     * структуру каталога товаров.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел со всеми потомками, или {@code null} если узел не найден.
     */
    @Override
    public Node findDepthNodeById(UUID id) {
        log.info("Start find depth node by id:{}", id);
        final Node result = findPlainNodeById(id);
        if (result == null) return result;
        if (NodeType.OFFER.equals(result.getType())) return result;
        Deque<Node> stack = new ArrayDeque<>();
        Map<UUID, Byte> colors = new HashMap<>();
        stack.push(result);
        colors.put(result.getId(), WHITE);
        while (!stack.isEmpty()) {
            Node parent = stack.pop();
            switch (colors.get(parent.getId())) {
                case WHITE -> {
                    colors.put(parent.getId(), GRAY);
                    stack.push(parent);
                    List<Node> children = findAllChildrenByIdTx(parent.getId());
                    parent.setChildren(children);
                    children.stream()
                            .filter(node -> NodeType.CATEGORY.equals(node.getType()))
                            .forEach(child -> {
                                stack.push(child);
                                colors.put(child.getId(), WHITE);
                            });
                }
                case GRAY -> {
                    colors.put(parent.getId(), BLACK);
                    if (parent.getChildren().isEmpty()) break;
                    final int sum = parent.getChildren()
                            .stream()
                            .mapToInt(Node::getSum)
                            .sum();
                    final int offerCount = parent.getChildren()
                            .stream()
                            .mapToInt(Node::getOfferCount)
                            .sum();
                    log.info("parent with ID: {} Sum -> {} OfferCount -> {}", parent.getParentId(), sum, offerCount);
                    parent.setSum(sum);
                    parent.setOfferCount(offerCount);
                    int price = sum;
                    if (offerCount != 0) {
                        price /= offerCount;
                    }
                    parent.setPrice(price);
                }
            }
        }
        log.info("Finish find depth node by id:{}", id);
        return result;
    }

    /**
     * Метод поиска узла без каких-либо потомков по идентификатору.
     * Метод возвращает узел без каких-либо потомков.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел каталога товаров, или {@code null} если узел не найден.
     */
    public Node findPlainNodeById(UUID id) {
        log.info("Start find plain node by id:{}", id);
        try {
            return jdbcTemplate.queryForObject("""
                            SELECT id, type, name, parent_id, price, to_char(date, 'yyyy-mm-dd hh24:mi:ss') as date
                               FROM node
                               WHERE id = ?::uuid""",
                    (rs, rowNum) -> new Node(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("type"),
                            rs.getString("name"),
                            rs.getString("parent_id") == null ? null : UUID.fromString(rs.getString("parent_id")),
                            rs.getObject("price", Integer.class),
                            rs.getTimestamp("date"),
                            null,
                            NodeType.OFFER.equals(rs.getString("type")) ? 1 : 0,
                            rs.getInt("price")
                    ),
                    id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Plain node with id:{} didn't find!", id);
            return null;
        }
    }

    /**
     * Приватный метод поиска прямых потомков узла по идентификатору,
     * для исполнения внутри транзакции.
     * Метод возвращает прямых потомков узла по указанному идентификатору.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return список прямых потомков узла.
     */
    private List<Node> findAllChildrenByIdTx(UUID id) {
        log.info("Start find children by node id:{}", id);
        return jdbcTemplate.query("""
                        SELECT id, type, name, parent_id, price, to_char(date, 'yyyy-mm-dd hh24:mi:ss') as date
                           FROM node
                           WHERE parent_id = ?::uuid""",
                (rs, rowNum) -> new Node(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("type"),
                        rs.getString("name"),
                        rs.getString("parent_id") == null ? null : UUID.fromString(rs.getString("parent_id")),
                        rs.getObject("price", Integer.class),
                        rs.getTimestamp("date"),
                        null,
                        NodeType.OFFER.equals(rs.getString("type")) ? 1 : 0,
                        rs.getInt("price")
                ),
                id);
    }


    /**
     * Реализация метода сохранения узла(товара/категории) в каталоге товаров,
     * путем добавленные новой позиций либо обновление текущей.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param node - узел, который необходимо добавить в каталог.
     * @return количество сохранённых позиций.
     */
    @Override
    public int save(Node node) {
        log.info("Start save node:{}", node.getId());
        return saveTx(node);
    }

    /**
     * Приватный метод сохранения узла(товара/категории) в каталоге товаров
     * для исполнения внутри транзакции.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param node - узел, который необходимо добавить в каталог.
     * @return количество сохранённых позиций.
     */
    private int saveTx(Node node) {
        final int count = jdbcTemplate.update("""
                        INSERT INTO 
                            node (
                                id, 
                                type, 
                                name, 
                                date, 
                                parent_id, 
                                price) 
                            VALUES(
                                ?::uuid, 
                                ?,  
                                ?, 
                                ?::timestamp with time zone, 
                                ?::uuid, 
                                ?)
                        ON CONFLICT (id) DO 
                            UPDATE SET
                                type = excluded.type,
                                name = excluded.name,
                                parent_id = excluded.parent_id,
                                price = excluded.price,
                                date = excluded.date""",
                node.getId(),
                node.getType(),
                node.getName(),
                node.getDate(),
                node.getParentId(),
                node.getPrice());
        log.info("Save node with ID: {} is successfully complete!", node.getId());
        return count;
    }

    /**
     * Реализация метода сохранения списка узлов(товаров/категории) в каталоге товаров,
     * путем добавленные новых либо обновление текущих.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param nodes - список узлов, который необходимо добавить в каталог.
     * @return количество сохранённых позиций.
     */
    @Override
    public int saveAll(List<Node> nodes) {
        log.info("Start save nodes!");
        int count = 0;
        for (Node node : nodes) {
            count += saveTx(node);
        }
        log.info("Finish save {} nodes!", count);
        return count;
    }

    /**
     * Реализация метода поиска идентификаторов имеющихся в каталоге категорий товаров.
     *
     * @return список идентификаторов категорий присутствующий в каталоге.
     */
    @Override
    public Set<UUID> findCategoryAllId() {
        return new HashSet<>(jdbcTemplate.queryForList("""
                        SELECT 
                            id 
                        FROM 
                            node 
                        WHERE 
                            type = ?""",
                UUID.class,
                NodeType.CATEGORY));
    }

    /**
     * Реализация метода удаления узла по идентификатору.
     * Метод удаляет только узел без потомков.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов.
     */
    @Override
    public int deleteNodeById(UUID id) {
        log.info("Start to delete only root node with ID: {}", id);
        return jdbcTemplate.update("""
                        DELETE FROM 
                            node 
                        WHERE 
                            id = ?::uuid;""",
                id);
    }

    /**
     * Метод удаляет всех потомков заданного узла.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов потомков.
     */
    public int deleteAllDescendantById(UUID id) {
        log.info("Start to delete all descendants node with ID: {}", id);
        return jdbcTemplate.update("""
                           WITH RECURSIVE r AS (
                           SELECT id, type, name, parent_id, price, date
                           FROM node
                           WHERE parent_id = ?::uuid
                           UNION
                           SELECT node.id, node.type, node.name, node.parent_id, node.price, node.date
                           FROM node
                              JOIN r
                                  ON node.parent_id = r.id
                        )
                        DELETE FROM 
                            node 
                        WHERE 
                            id in (SELECT id FROM r)""",
                id);
    }
}
