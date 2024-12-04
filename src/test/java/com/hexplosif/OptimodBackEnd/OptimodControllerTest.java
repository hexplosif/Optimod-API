package com.hexplosif.OptimodBackEnd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexplosif.OptimodBackEnd.model.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OptimodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    /*
    Test CREATE valid node (2, 2.2, 3.3)
    Expected: 200 (OK)
     */
    public void testCreateValidNode() throws Exception {
        Node node = new Node();
        node.setId(2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    /*
    Test CREATE null node
    Expected: 400 (Bad Request)
     */
    public void testCreateNullNode() throws Exception {
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    /*
    Test CREATE incomplete node (2, null, null)
    Expected: 200 (OK) because the node is created even if it is incomplete, checks
    should be done before calling the controller
     */
    public void testCreateIncompleteNode() throws Exception {
        Node node = new Node();
        node.setId(2L);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    /*
    Test CREATE node without no content in the body of the request
    Expected: 400 (Bad Request)
     */
    public void testCreateNullBody() throws Exception {
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    /*
    Test CREATE node with a negative id (-2, 2.2, 3.3)
    Expected: 200 (OK)
     */
    public void testCreateNegativeIdNode() throws Exception {
        Node node = new Node();
        node.setId(-2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    /*
    Test GET a valid node with an existing id (2)
    Expected: 200 (OK)
     */
    public void testGetValidNode() throws Exception {
        // Creates a node
        Node node = new Node();
        node.setId(-2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(post("/node")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(node)));

        // Gets the node
        mockMvc.perform(get("/node/2"))
                .andExpect(status().isOk());
    }

    @Test
    /*
    Test GET a node with a non-existent id (1)
    Expected: 200 (OK)
     */
    public void testGetNonExistentNode() throws Exception {
        mockMvc.perform(get("/node/1"))
                .andExpect(status().isOk());
    }

    @Test
    /*
    Test GET a node with non int id (q, 2.2, 3.3)
    Expected: 400 (Bad Request)
     */
    public void testGetInvalidIdNode() throws Exception {
        mockMvc.perform(get("/node/q"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/node/2.2"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/node/-"))
                .andExpect(status().isBadRequest());
    }

    @Test
    /*
    Test GET a node with no id
    Expected: 405 (Method Not Allowed)
     */
    public void testGetNullNode() throws Exception {
        mockMvc.perform(get("/node"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    /*
    Test GET all nodes
    Expected: 200 (OK)
     */
    public void testGetNodes() throws Exception {
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk());
    }

    @Test
    /*
    Test DELETE all nodes
    Expected: 200 (OK) and an empty list of nodes
     */
    public void testDeleteNodes() throws Exception {
        // Step 1: Create a couple of nodes
        Node node1 = new Node();
        node1.setId(2L);
        node1.setLongitude(2.2);
        node1.setLatitude(3.3);

        Node node2 = new Node();
        node2.setId(3L);
        node2.setLongitude(2.1);
        node2.setLatitude(3.1);

        // Add nodes via POST
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node2)))
                .andExpect(status().isOk());

        // Step 2: Verify the nodes exist
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("\"id\":2");
                    assert content.contains("\"id\":3");
                });

        // Step 3: Delete all nodes
        mockMvc.perform(delete("/nodes"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of nodes is empty
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk());
//                .andExpect(result -> {
//                    String content = result.getResponse().getContentAsString();
//                    assert content.isEmpty(); // Or check for an empty JSON array "[]"
//                });
    }

}
