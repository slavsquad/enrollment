package org.product.catalog.analyzer.enrollment.repository;

import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Интерфейс, описывающий методы взаимодействия приложения с базой данных.
 *
 * @author Stepanenko Stanislav
 */
public interface NodeRepository {

    /**
     * Поиск узла в полную глубину по идентификатору.
     * Метод возвращает узел со всеми потомками, полностью отображая
     * структуру каталога товаров.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел со всеми потомками, или {@code null} если узел не найден.
     */
    Node findDepthNodeById(UUID id);

    /**
     * Поиск узла без каких-либо потомков по идентификатору.
     * Метод возвращает узел без каких-либо потомков.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел каталога товаров, или {@code null} если узел не найден.
     */
    public Node findPlainNodeById(UUID id);

    /**
     * Сохранение узла(товара/категории) в каталоге товаров,
     * путем добавленные новой позиций либо обновление текущей.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param node - узел, который необходимо добавить в каталог.
     * @return количество сохранённых позиций.
     */
    int save(Node node);

    /**
     * Сохранение списка узлов(товаров/категории) в каталоге товаров,
     * путем добавленные новых либо обновление текущих.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param nodes - список узлов, который необходимо добавить в каталог.
     * @return количество сохранённых позиций.
     */
    int saveAll(List<Node> nodes);

    /**
     * Удаление узла по идентификатору.
     * Метод удаляет только узел без потомков.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов.
     */
    int deleteNodeById(UUID id);

    /**
     * Удаление всех потомков заданного узла.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов потомков.
     */
    int deleteAllDescendantById(UUID id);
}
