package org.product.catalog.analyzer.enrollment.controller;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
class NodeControllerTest {

    final String importRequest = """
               {
            "items": [
                       {
                           "type": "CATEGORY",
                           "name": "Товары",
                           "id": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1",
                           "parentId": null
                       },
                       {
                           "type": "CATEGORY",
                           "name": "Смартфоны",
                           "id": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                           "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"
                       },
                       {
                           "type": "OFFER",
                           "name": "jPhone 13",
                           "id": "863e1a7a-1304-42ae-943b-179184c077e3",
                           "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                           "price": 79999
                       },
                       {
                           "type": "OFFER",
                           "name": "Xomiа Readme 10",
                           "id": "b1d8fd7d-2ae3-47d5-b2f9-0f094af800d4",
                           "parentId": "d515e43f-f3f6-4471-bb77-6b455017a2d2",
                           "price": 59999
                       },
                       {
                           "type": "CATEGORY",
                           "name": "Телевизоры",
                           "id": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                           "parentId": "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"
                       },
                       {
                           "type": "OFFER",
                           "name": "Samson 70\\" LED UHD Smart",
                           "id": "98883e8f-0507-482f-bce2-2fb306cf6483",
                           "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                           "price": 32999
                       },
                       {
                           "type": "OFFER",
                           "name": "Phyllis 50\\" LED UHD Smarter",
                           "id": "74b81fda-9cdc-4b63-8927-c978afed5cf4",
                           "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                           "price": 49999
                       },
                       {
                           "type": "OFFER",
                           "name": "Goldstar 65\\" LED UHD LOL Very Smart",
                           "id": "73bc3b36-02d1-4245-ab35-3106c9ee1c65",
                           "parentId": "1cc0129a-2bfe-474c-9ee6-d435bf5fc8f2",
                           "price": 69999
                       }
                   ],
                   "updateDate": "1999-09-09T23:00:00.000Z"
               }""";
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/imports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(importRequest));
    }

    @AfterEach
    void tearDown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/delete/069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"));
    }

    @Test
    void importNodes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/imports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(importRequest))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getNode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nodes/069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteNode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete//069cb8d7-bbdd-47d3-ad8f-82ef4c269df1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}