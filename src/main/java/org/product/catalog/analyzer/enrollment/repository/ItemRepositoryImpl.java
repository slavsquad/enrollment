package org.product.catalog.analyzer.enrollment.repository;

import lombok.AllArgsConstructor;
import org.product.catalog.analyzer.enrollment.dto.Item;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@AllArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int save(Item item) {

        return jdbcTemplate.update("""
                        INSERT INTO 
                            item (
                                id, 
                                type, 
                                name, 
                                update_date, 
                                parent_id, 
                                price) 
                            VALUES(
                                ?::uuid, 
                                ?,  
                                ?, 
                                ?::timestamp without time zone, 
                                ?::uuid, 
                                ?)
                        ON CONFLICT (id) DO 
                            UPDATE SET
                                type = excluded.type,
                                name = excluded.name,
                                parent_id = excluded.parent_id,
                                price = excluded.price,
                                update_date = excluded.update_date""",
                item.getId(),
                item.getType(),
                item.getName(),
                item.getUpdateDate(),
                item.getParentId(),
                item.getPrice());
    }

    @Override
    public Set<String> findCategoryListId() {
        return null;
    }
}
