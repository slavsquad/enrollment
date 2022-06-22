package org.product.catalog.analyzer.enrollment.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Repository
@AllArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Optional<Node> findById(UUID id) {
        return findByIdTx(id);
    }

    @Override
    @Transactional
    public int save(Node node) {
        return saveTx(node);
    }

    @Override
    @Transactional
    public int saveAll(List<Node> nodes) {
        log.info("Start save nodes!");
        int count = 0;
        for (Node node : nodes) {
            count += saveTx(node);
        }
        log.info("Finish save {} nodes!", count);
        return count;
    }

    private Optional<Node> findByIdTx(UUID id){
        try {
            return jdbcTemplate.queryForObject("""
                            SELECT id, type, name, parent_id, price, to_char(date, 'yyyy-mm-dd hh24:mi:ss') as date
                               FROM node
                               WHERE id = ?::uuid""",
                    (rs, rowNum) -> Optional.of(new Node(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("type"),
                            rs.getString("name"),
                            rs.getString("parent_id") == null ? null : UUID.fromString(rs.getString("parent_id")),
                            rs.getInt("price"),
                            rs.getTimestamp("date"),
                            null
                    )),
                    id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Node with id:{} didn't find!", id);
            return Optional.empty();
        }
    }
    private int saveTx(Node node) {
        int count = jdbcTemplate.update("""
                        INSERT INTO 
                            node (
                                id, 
                                type, 
                                name, 
                                date, 
                                parent_id, 
                                price) 
                            VALUES(
                                ?::uuid, 
                                ?,  
                                ?, 
                                ?::timestamp with time zone, 
                                ?::uuid, 
                                ?)
                        ON CONFLICT (id) DO 
                            UPDATE SET
                                type = excluded.type,
                                name = excluded.name,
                                parent_id = excluded.parent_id,
                                price = excluded.price,
                                date = excluded.date""",
                node.getId(),
                node.getType(),
                node.getName(),
                node.getDate(),
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
                            node 
                        WHERE 
                            type = 'CATEGORY'""",
                UUID.class));
    }
}
