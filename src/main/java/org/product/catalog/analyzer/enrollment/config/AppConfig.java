package org.product.catalog.analyzer.enrollment.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Конфигурационный класс, предназначен для настройки взаимодействия приложения
 * с различными компонентами в частности базой данных.
 *
 * @author Stepanenko Stanislav
 */
@Configuration
public class AppConfig {

    /**
     * Метод создает источник данных так называемый DataSource, для взаимодействия с базой данных.
     *
     * @return - источник данных.
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public HikariDataSource hikariDataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * Метод создает некий "адаптер" именуемый jdbcTemplate,
     * с помощью которого приложение будет взаимодействовать с источником данных.
     *
     * @param hikariDataSource - источник данных.
     * @return - "адаптер" именуемый jdbcTemplate.
     */
    @Bean
    public JdbcTemplate jdbcTemplate(HikariDataSource hikariDataSource) {
        return new JdbcTemplate(hikariDataSource);
    }
}