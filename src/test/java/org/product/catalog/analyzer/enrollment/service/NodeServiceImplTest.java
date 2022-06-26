package org.product.catalog.analyzer.enrollment.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.dto.NodeType;
import org.product.catalog.analyzer.enrollment.repository.NodeRepository;
import org.product.catalog.analyzer.enrollment.validation.exception.ArgumentNotValidException;
import org.product.catalog.analyzer.enrollment.validation.exception.NotFindNodeException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NodeServiceImplTest {

    @Mock
    private NodeRepository nodeRepository;
    private NodeService underTestService;

    @BeforeEach
    void setUp() {
        underTestService = new NodeServiceImpl(nodeRepository);
    }


    @Test
    void findById() throws NotFindNodeException {
        final UUID nokiaId = UUID.randomUUID();

        given(nodeRepository.findDepthNodeById(nokiaId))
                .willReturn(new Node(
                        nokiaId,
                        NodeType.OFFER,
                        "Nokia 3310",
                        null,
                        null,
                        null,
                        new Date(),
                        new ArrayList<>(),
                        1,
                        69999));

        assertThat(underTestService.findById(nokiaId).getId()).isEqualTo(nokiaId);
    }

    @Test
    void findThrowWhenNotFound() {

        final UUID nokiaId = UUID.randomUUID();

        given(nodeRepository.findDepthNodeById(nokiaId))
                .willReturn(null);

        assertThatThrownBy(() -> underTestService.findById(nokiaId))
                .isInstanceOf(NotFindNodeException.class)
                .hasMessageContaining("Node with id: " + nokiaId + " didn't find!");
    }

    @Test
    void importNodes() throws ArgumentNotValidException {
        final Node phoneCategory = new Node(
                UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телефоны",
                null,
                null,
                null,
                new Date(),
                new ArrayList<>(),
                0,
                0);
        final Node nokia = new Node(
                UUID.randomUUID(),
                NodeType.OFFER,
                "Nokia 3310",
                phoneCategory.getId(),
                phoneCategory.getId(),
                69999,
                phoneCategory.getDate(),
                new ArrayList<>(),
                1,
                69999);
        List<Node> importNodeList = Arrays.asList(phoneCategory, nokia);
        underTestService.importNodes(importNodeList);
        ArgumentCaptor<List<Node>> importNodeCaptorList = ArgumentCaptor.forClass(List.class);
        verify(nodeRepository).saveAll(importNodeCaptorList.capture());
        List<Node> capturedNodeList = importNodeCaptorList.getValue();
        assertThat(capturedNodeList).isEqualTo(importNodeList);
    }

    @Test
    void willThrowWhenHasDuplicateId() throws ArgumentNotValidException {
        final UUID duplicateId = UUID.randomUUID();
        final Node phoneCategory = new Node(
                duplicateId,
                NodeType.CATEGORY,
                "Телефоны",
                null,
                null,
                null,
                new Date(),
                new ArrayList<>(),
                0,
                0);
        final Node nokia = new Node(
                duplicateId,
                NodeType.OFFER,
                "Nokia 3310",
                phoneCategory.getId(),
                phoneCategory.getId(),
                69999,
                phoneCategory.getDate(),
                new ArrayList<>(),
                1,
                69999);
        List<Node> importNodeList = Arrays.asList(phoneCategory, nokia);

        assertThatThrownBy(() -> underTestService.importNodes(importNodeList))
                .isInstanceOf(ArgumentNotValidException.class)
                .hasMessageContaining("Import records contains duplicates id!");
    }

    @Test
    void willThrowWhenCategoryPriceNotNull() throws ArgumentNotValidException {
        final Node phoneCategory = new Node(
                UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телефоны",
                null,
                null,
                99999,
                new Date(),
                new ArrayList<>(),
                0,
                0);
        final Node nokia = new Node(
                UUID.randomUUID(),
                NodeType.OFFER,
                "Nokia 3310",
                phoneCategory.getId(),
                phoneCategory.getId(),
                69999,
                phoneCategory.getDate(),
                new ArrayList<>(),
                1,
                69999);
        List<Node> importNodeList = Arrays.asList(phoneCategory, nokia);

        assertThatThrownBy(() -> underTestService.importNodes(importNodeList))
                .isInstanceOf(ArgumentNotValidException.class)
                .hasMessageContaining("Category price must be null!");
    }

    @Test
    void willThrowWhenOfferPriceNull() throws ArgumentNotValidException {
        final Node phoneCategory = new Node(
                UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телефоны",
                null,
                null,
                null,
                new Date(),
                new ArrayList<>(),
                0,
                0);
        final Node nokia = new Node(
                UUID.randomUUID(),
                NodeType.OFFER,
                "Nokia 3310",
                phoneCategory.getId(),
                phoneCategory.getId(),
                null,
                phoneCategory.getDate(),
                new ArrayList<>(),
                1,
                69999);
        List<Node> importNodeList = Arrays.asList(phoneCategory, nokia);

        assertThatThrownBy(() -> underTestService.importNodes(importNodeList))
                .isInstanceOf(ArgumentNotValidException.class)
                .hasMessageContaining("Offer price must not be null or be positive number!");
    }

    @Test
    void willThrowWhenChangeTypeNode() throws ArgumentNotValidException {
        final Node phoneCategory = new Node(
                UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телефоны",
                null,
                null,
                null,
                new Date(),
                new ArrayList<>(),
                0,
                0);
        final Node nokia = new Node(
                UUID.randomUUID(),
                NodeType.OFFER,
                "Nokia 3310",
                phoneCategory.getId(),
                phoneCategory.getId(),
                null,
                phoneCategory.getDate(),
                new ArrayList<>(),
                1,
                69999);
        List<Node> importNodeList = Arrays.asList(phoneCategory, nokia);

        given(nodeRepository.findPlainNodeById(phoneCategory.getId()))
                .willReturn(new Node(
                        UUID.randomUUID(),
                        NodeType.OFFER,
                        "Телефоны",
                        null,
                        null,
                        null,
                        new Date(),
                        new ArrayList<>(),
                        0,
                        0));

        assertThatThrownBy(() -> underTestService.importNodes(importNodeList))
                .isInstanceOf(ArgumentNotValidException.class)
                .hasMessageContaining("Change node type is not allowed!");
    }

    @Test
    void willThrowWhenParentIsNotCategoryTypeNode() throws ArgumentNotValidException {
        final UUID nokiaId = UUID.randomUUID();
        final Node phoneCategory = new Node(
                UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телефоны",
                nokiaId,
                nokiaId,
                null,
                new Date(),
                new ArrayList<>(),
                0,
                0);
        final Node nokia = new Node(
                nokiaId,
                NodeType.OFFER,
                "Nokia 3310",
                null,
                null,
                null,
                phoneCategory.getDate(),
                new ArrayList<>(),
                1,
                69999);
        List<Node> importNodeList = Arrays.asList(phoneCategory, nokia);

        given(nodeRepository.findPlainNodeById(phoneCategory.getId()))
                .willReturn(null);

        given(nodeRepository.findPlainNodeById(phoneCategory.getParentId()))
                .willReturn(new Node(
                        nokiaId,
                        NodeType.OFFER,
                        "Nokia 3310",
                        null,
                        null,
                        null,
                        phoneCategory.getDate(),
                        new ArrayList<>(),
                        1,
                        69999));

        assertThatThrownBy(() -> underTestService.importNodes(importNodeList))
                .isInstanceOf(ArgumentNotValidException.class)
                .hasMessageContaining("Category with ID: " + phoneCategory.getParentId() + " is didn't find or node is not a category!");
    }

    @Test
    void deleteById() throws NotFindNodeException {
        final UUID phoneCategoryId = UUID.randomUUID();
        final int descendantCount = 3;
        final int ancestorCount = 1;

        given(nodeRepository.findPlainNodeById(phoneCategoryId))
                .willReturn(new Node(
                        phoneCategoryId,
                        NodeType.CATEGORY,
                        "Телефоны",
                        null,
                        null,
                        null,
                        new Date(),
                        new ArrayList<>(),
                        0,
                        0));

        given(nodeRepository.deleteAllDescendantById(phoneCategoryId))
                .willReturn(descendantCount);

        given(nodeRepository.deleteNodeById(phoneCategoryId))
                .willReturn(ancestorCount);

        assertThat(underTestService.deleteById(phoneCategoryId))
                .isEqualTo(ancestorCount + descendantCount);
    }

    @Test
    void deleteThrowWhenNotFound() {

        final UUID nokiaId = UUID.randomUUID();

        given(nodeRepository.findPlainNodeById(nokiaId))
                .willReturn(null);

        assertThatThrownBy(() -> underTestService.deleteById(nokiaId))
                .isInstanceOf(NotFindNodeException.class)
                .hasMessageContaining("Node with id: " + nokiaId + " didn't find!");
    }
}