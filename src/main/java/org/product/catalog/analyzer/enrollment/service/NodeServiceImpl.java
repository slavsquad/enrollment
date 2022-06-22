package org.product.catalog.analyzer.enrollment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.product.catalog.analyzer.enrollment.dto.Node;
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
    public void addNode(Node node) {
        log.info("Node with id:{} is ready to import!", node.getId());
        nodeRepository.save(node);
    }

    @Override
    public Node findById(UUID id) {
        log.info("Looking for node by id: {}", id);
        return nodeRepository.findById(id);
    }

    @Override
    public void importNodes(List<Node> nodes, Date updateDate) {
        for (Node item : nodes) {
            item.setDate(updateDate);
        }
        log.info("{} nodes are ready to import!", nodes.size());
        nodeRepository.saveAll(nodes);
    }

    @Override
    public Set<UUID> findCategoryAllId() {
        final Set<UUID> categoryIdSet = nodeRepository.findCategoryAllId();
        log.info("Find {} id category from repository!", categoryIdSet.size());
        return categoryIdSet;
    }
}
