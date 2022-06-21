package org.product.catalog.analyzer.enrollment.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Repository
@AllArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public int save(ImportNode node) {
        return saveTx(node);
    }

    @Override
    @Transactional
    public int saveAll(List<ImportNode> nodes) {
        log.info("Start save nodes!");
        int count = 0;
        for (ImportNode node : nodes) {
            count += saveTx(node);
        }
        log.info("Finish save {} nodes!", count);
        return count;
    }

    private int saveTx(ImportNode node) {
        int count = jdbcTemplate.update("""
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
                node.getId(),
                node.getType(),
                node.getName(),
                node.getUpdateDate(),
                node.getParentId(),
                node.getPrice());
        log.info("Save: {} is successfully complete!", node);
        return count;
    }

    @Override
    public Set<UUID> findCategoryAllId() {
        return new HashSet<>(jdbcTemplate.queryForList("""
                        SELECT 
                            id 
                        FROM 
                            item 
                        WHERE 
                            type = 'CATEGORY'""",
                UUID.class));
    }
}
