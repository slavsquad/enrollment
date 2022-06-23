package org.product.catalog.analyzer.enrollment.validation.exception;


/**
 * Класс, реализующий исключение,
 * которое выбрасывается в случае если аргумент запроса не прошел проверку.
 *
 * @author Stepanenko Stanislav
 */
public class ArgumentNotValidException extends Exception{

    /**
     * Конструктор исключения.
     *
     * @param message - сообщение.
     */
    public ArgumentNotValidException(String message) {
        super(message);
    }
}
