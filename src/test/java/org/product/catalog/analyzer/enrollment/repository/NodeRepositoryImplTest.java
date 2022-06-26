package org.product.catalog.analyzer.enrollment.repository;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product.catalog.analyzer.enrollment.dto.Node;
import org.product.catalog.analyzer.enrollment.dto.NodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@DataJdbcTest
@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class NodeRepositoryImplTest {

    JdbcTemplate jdbcTemplate;
    NodeRepository underTestRepository;
    final Date updateDate = new Date();
    Node expectedNode;

    @Autowired
    public NodeRepositoryImplTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.underTestRepository = new NodeRepositoryImpl(jdbcTemplate);
    }

    @BeforeEach
    public void setUp() {

        expectedNode = new Node(UUID.randomUUID(),
                NodeType.CATEGORY,
                "Товары",
                null,
                null,
                null,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(expectedNode);

        final Node phoneCategory = new Node(UUID.randomUUID(),
                NodeType.CATEGORY,
                "Смартфоны",
                expectedNode.getId(),
                expectedNode.getId(),
                null,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(phoneCategory);

        final Node jPhone = new Node(UUID.randomUUID(),
                NodeType.OFFER,
                "jPhone 13",
                phoneCategory.getId(),
                phoneCategory.getId(),
                79999,
                updateDate,
                null,
                0,
                0);
        underTestRepository.save(jPhone);
        jPhone.setOfferCount(1);
        jPhone.setSum(jPhone.getPrice());

        final Node xomiаPhone = new Node(UUID.randomUUID(),
                NodeType.OFFER,
                "Xomiа Readme 10",
                phoneCategory.getId(),
                phoneCategory.getId(),
                59999,
                updateDate,
                null,
                0,
                0);
        underTestRepository.save(xomiаPhone);
        xomiаPhone.setOfferCount(1);
        xomiаPhone.setSum(xomiаPhone.getPrice());

        final List<Node> phoneChildren = Arrays.asList(jPhone, xomiаPhone);
        phoneCategory.setChildren(phoneChildren);
        phoneCategory.setSum(phoneChildren.stream().mapToInt(Node::getSum).sum());
        phoneCategory.setOfferCount(phoneChildren.stream().mapToInt(Node::getOfferCount).sum());
        phoneCategory.setPrice(phoneCategory.getSum() / phoneCategory.getOfferCount());

        final Node tvCategory = new Node(UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телевизоры",
                expectedNode.getId(),
                expectedNode.getId(),
                null,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(tvCategory);

        final Node samsonTv = new Node(UUID.randomUUID(),
                NodeType.OFFER,
                "Samson 70\\\" LED UHD Smart",
                tvCategory.getId(),
                tvCategory.getId(),
                32999,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(samsonTv);
        samsonTv.setOfferCount(1);
        samsonTv.setSum(samsonTv.getPrice());

        final Node phyllisTv = new Node(UUID.randomUUID(),
                NodeType.OFFER,
                "Phyllis 50\\\" LED UHD Smarter",
                tvCategory.getId(),
                tvCategory.getId(),
                49999,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(phyllisTv);
        phyllisTv.setOfferCount(1);
        phyllisTv.setSum(phyllisTv.getPrice());

        final Node goldstarTv = new Node(UUID.randomUUID(),
                NodeType.OFFER,
                "Goldstar 65\\\" LED UHD LOL Very Smart",
                tvCategory.getId(),
                tvCategory.getId(),
                69999,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(goldstarTv);
        goldstarTv.setOfferCount(1);
        goldstarTv.setSum(goldstarTv.getPrice());

        final List<Node> tvChildren = Arrays.asList(samsonTv, phyllisTv, goldstarTv);
        tvCategory.setChildren(tvChildren);
        tvCategory.setSum(tvChildren.stream().mapToInt(Node::getSum).sum());
        tvCategory.setOfferCount(tvChildren.stream().mapToInt(Node::getOfferCount).sum());
        tvCategory.setPrice(tvCategory.getSum() / tvCategory.getOfferCount());

        final List<Node> goodChildren = Arrays.asList(tvCategory, phoneCategory);
        expectedNode.setChildren(goodChildren);
        expectedNode.setSum(goodChildren.stream().mapToInt(Node::getSum).sum());
        expectedNode.setOfferCount(goodChildren.stream().mapToInt(Node::getOfferCount).sum());
        expectedNode.setPrice(expectedNode.getSum() / expectedNode.getOfferCount());
    }

    @AfterEach
    public void tearDown() {
        underTestRepository.deleteAllDescendantById(expectedNode.getId());
        underTestRepository.deleteNodeById(expectedNode.getId());
        expectedNode = null;
    }

    @Test
    void findDepthNodeById() {

        final Node actualNode = underTestRepository.findDepthNodeById(expectedNode.getId());

        assertThat(actualNode).isNotNull();

        Deque<Node> expectedStack = new ArrayDeque<>();
        Deque<Node> actualStack = new ArrayDeque<>();
        expectedStack.push(expectedNode);
        actualStack.push(actualNode);

        while (!expectedStack.isEmpty() && !actualStack.isEmpty()) {

            final Node expectedParent = expectedStack.pop();
            final Node actualParent = actualStack.pop();

            assertThat(actualParent)
                    .usingRecursiveComparison()
                    .withStrictTypeChecking()
                    .ignoringFields("children", "date") //compared below
                    .isEqualTo(expectedParent);

            assertThat(actualParent.getDate()).isCloseTo(expectedParent.getDate(), 1000);

            if (expectedParent.getChildren() != null && actualParent.getChildren() != null) {

                final List<Node> expectedChildren = expectedParent.getChildren()
                        .stream()
                        .sorted(Comparator.comparingInt(o -> o.getId().hashCode()))
                        .toList();

                final List<Node> actualChildren = actualParent.getChildren()
                        .stream()
                        .sorted(Comparator.comparingInt(o -> o.getId().hashCode()))
                        .toList();

                for (int i = 0; i < Math.max(expectedChildren.size(), actualChildren.size()); i++) {
                    expectedStack.push(expectedChildren.get(i));
                    actualStack.push(actualChildren.get(i));
                }
            }
        }

        assertThat(underTestRepository.findDepthNodeById(UUID.randomUUID())).isNull();
    }

    @Test
    void findPlainNodeById() {

        final Node actualNode = underTestRepository.findPlainNodeById(expectedNode.getId());

        assertThat(actualNode).isNotNull();

        assertThat(actualNode)
                .usingRecursiveComparison()
                .withStrictTypeChecking()
                .ignoringFields("children", "date", "offerCount", "sum", "price") //because is a plain Node
                .isEqualTo(expectedNode);

        assertThat(actualNode.getDate()).isCloseTo(expectedNode.getDate(), 1000);

        assertThat(underTestRepository.findPlainNodeById(UUID.randomUUID())).isNull();
    }

    @Test
    void save() {
/*        final Node newCategory = new Node(UUID.randomUUID(),
                NodeType.CATEGORY,
                "Телевизоры",
                expectedNode.getId(),
                expectedNode.getId(),
                null,
                updateDate,
                new ArrayList<>(),
                0,
                0);
        underTestRepository.save(tvCategory);*/
    }

    @Test
    void saveAll() {
    }

    @Test
    void deleteNodeById() {

        assertThat(underTestRepository.findPlainNodeById(expectedNode.getId())).isNotNull();
        assertThat(underTestRepository.deleteNodeById(expectedNode.getId())).isEqualTo(1);
        assertThat(underTestRepository.findPlainNodeById(expectedNode.getId())).isNull();
        expectedNode.getChildren()
                .forEach(child -> assertThat(underTestRepository.findPlainNodeById(child.getId()))
                        .isNotNull());

    }

    @Test
    void deleteAllDescendantById() {

        Node deleteCandidate = underTestRepository.findDepthNodeById(expectedNode.getId());
        assertThat(deleteCandidate).isNotNull();

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(deleteCandidate);
        int allChildrenCount = -1;
        while (!stack.isEmpty()){
            allChildrenCount++;
            final Node parent = stack.pop();
            if (parent.getChildren() != null){
                for (Node child:parent.getChildren()){
                    stack.push(child);
                    assertThat(underTestRepository.findPlainNodeById(child.getId())).isNotNull();
                }
            }
        }

        assertThat(underTestRepository.deleteAllDescendantById(deleteCandidate.getId())).isEqualTo(allChildrenCount);

        stack.push(deleteCandidate);
        while (!stack.isEmpty()){
            final Node parent = stack.pop();
            if (parent.getChildren() != null){
                for (Node child:parent.getChildren()){
                    stack.push(child);
                    assertThat(underTestRepository.findPlainNodeById(child.getId())).isNull();
                }
            }
        }
    }
}