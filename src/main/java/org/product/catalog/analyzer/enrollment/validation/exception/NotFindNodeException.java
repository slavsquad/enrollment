package org.product.catalog.analyzer.enrollment.validation.exception;

/**
 * Класс, реализующий исключение,
 * которое выбрасывается, в случае если элемент не найден в каталоге товаров.
 *
 * @author Stepanenko Stanislav
 */
public class NotFindNodeException extends Exception{

    /**
     * Конструктор исключения.
     *
     * @param message - сообщение.
     */
    public NotFindNodeException(String message) {
        super(message);
    }
}
