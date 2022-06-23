package org.product.catalog.analyzer.enrollment.config;

import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Конфигурационный класс, предназначен для настройки автоматического описания
 * методов взаимодействия с приложением.
 *
 * @author Stepanenko Stanislav
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    /**
     * Метод создает конструктор, который служит для дальнейшего построения интерфейса,
     * инструмента автоматического описания методов взаимодействия с приложением(Springfox framework).
     *
     * @return - конструктор для построения интерфеса, инструмента автоматического описания
     * методов взаимодействия с приложением(Springfox framework)
     */
    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.product.catalog.analyzer.enrollment"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * Приватный метод создает информационное отображение, инструмента автоматического описания
     * методов взаимодействия с приложением(Springfox framework).
     *
     * @return - информационное отображение инструмента, автоматического описания
     * методов взаимодействия с приложением(Springfox framework)
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Mega Market Open API")
                .description("Вступительное задание в Летнюю Школу Бэкенд Разработки Яндекса 2022")
                .version("1.0.0")
                .build();
    }
}