package org.product.catalog.analyzer.enrollment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.ImportNode;
import org.product.catalog.analyzer.enrollment.repository.NodeRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;

    @Override
    public void addItem(ImportNode node) {
        log.info("Item with id:{} is ready to import!", node.getId());
        nodeRepository.save(node);
    }

    @Override
    public void importNodes(List<ImportNode> nodes, Date updateDate) {
        for (ImportNode item : nodes) {
            item.setUpdateDate(updateDate);
            if ("CATEGORY".equals(item.getType())) {
                item.setPrice(0);
            }
        }
        log.info("{} items are ready to import!", nodes.size());
        nodeRepository.saveAll(nodes);
    }

    @Override
    public Set<UUID> findCategoryAllId() {
        final Set<UUID> categoryIdSet = nodeRepository.findCategoryAllId();
        log.info("Find category all id : {}", categoryIdSet);
        return categoryIdSet;
    }
}
