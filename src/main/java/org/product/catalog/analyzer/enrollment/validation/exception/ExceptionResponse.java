package org.product.catalog.analyzer.enrollment.validation.exception;

/**
 * Класс, реализованный в виде record, выступает в качестве структуры
 * в которой храниться JSON-ответ на перехваченные исключения.
 *
 * @param code - код http статуса.
 * @param message - сообщение.
 * @author Stepanenko Stanislav
 */
public record ExceptionResponse(int code, String message) {
}
