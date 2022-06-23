package org.product.catalog.analyzer.enrollment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения, описывает "входную точку" от куда начинается запуск.
 *
 * @author Stepanenko Stanislav
 */
@SpringBootApplication
public class EnrollmentApplication {

	/**
	 * Метод описывает "входную точку" от куда начинается старт приложения.
	 * с помощью которого приложение будет взаимодействовать с источником данных.
	 *
	 * @param args - массив аргументов который передается основному методу приложения.
	 */
	public static void main(String[] args) {
		SpringApplication.run(EnrollmentApplication.class, args);
	}

}
