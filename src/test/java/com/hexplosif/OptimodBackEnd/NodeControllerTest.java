package com.hexplosif.OptimodBackEnd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexplosif.OptimodBackEnd.model.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateNode() throws Exception {
        Node node = new Node();
        node.setId(2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(post("/node")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(node)))  // serialize the object
                .andExpect(status().isOk());
    }

    @Test
    public void testGetNode() throws Exception {
        mockMvc.perform(get("/node/2"));
    }

    @Test
    public void testGetNodes() throws Exception {
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
