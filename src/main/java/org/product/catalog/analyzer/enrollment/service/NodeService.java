package org.product.catalog.analyzer.enrollment.service;

import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.product.catalog.analyzer.enrollment.validation.exception.NotFindNodeException;

import java.util.*;

/**
 * Интерфейс, описывающий сервисные методы вставки, поиска и удаления узлов.
 *
 * @author Stepanenko Stanislav
 */
public interface NodeService {

    /**
     * Вставка узла(товара/категории) в каталоге товаров,
     * путем добавленные новой позиций либо обновление текущей.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param node - узел, который необходимо добавить в каталог.
     */
    void addNode(Node node);

    /**
     * Поиск узла в полную глубину по идентификатору.
     * Метод возвращает узел со всеми потомками, полностью отображая
     * структуру каталога товаров.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return узел со всеми потомками, или {@code null} если узел не найден.
     * @throws NotFindNodeException если элемент не найден.
     */
    Node findById(UUID id) throws NotFindNodeException;

    /**
     * Импортирование узлов(товаров/категории) в каталоге товаров,
     * путем добавленные новых либо обновление текущих.
     * Метод возвращает количество сохраненных позиций.
     *
     * @param nodes - список узлов, который необходимо добавить в каталог.
     * @param updateDate - дата импорта узлов в каталог.
     * @throws ArgumentNotValidException если какой либо из узел не прошел проверку.
     */
    void importNodes(List<Node> nodes, Date updateDate) throws ArgumentNotValidException;

    /**
     * Поиск идентификаторов имеющихся в каталоге категорий товаров.
     *
     * @return список идентификаторов категорий присутствующий в каталоге.
     */
    Set<UUID> findCategoryAllId();

    /**
     * Удаление узла по идентификатору.
     * Метод удаляет узел со всеми потомками если таковые имеются.
     *
     * @param id - идентификатор корневого узла(товара/категории).
     * @return количество удалённых узлов.
     * @throws NotFindNodeException если элемент не найден.
     */
    int deleteById(UUID id) throws NotFindNodeException;
}
