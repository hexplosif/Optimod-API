package com.hexplosif.OptimodBackEnd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import com.hexplosif.OptimodBackEnd.model.Node;
import com.hexplosif.OptimodBackEnd.model.Segment;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    @Tag("NodeController")
    /*
    Test CREATE valid node (id=2, latitude=2.2, longitude=3.3)
    Expected: 200 (OK)
     */
    public void testCreateNodeValid() throws Exception {
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
    @Tag("NodeController")
    /*
    Test CREATE incomplete node (id=2, latitude=null, longitude=null)
    Expected: 200 (OK) because the node is created even if it is incomplete, checks should be made before calling the controller
     */

    public void testCreateNodeIncomplete() throws Exception {
        Node node = new Node();
        node.setId(2L);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("NodeController")
    /*
    Test CREATE node with an empty request body
    Expected: 400 (Bad Request)
     */
    public void testCreateNodeEmptyBody() throws Exception {
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("NodeController")
    /*
    Test CREATE node with a negative id (id=-2)
    Expected: 200 (OK)
     */
    public void testCreateNodeNegativeId() throws Exception {
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
    @Tag("NodeController")
    /*
    Test GET a valid node with an existing id (id=2)
    Expected: 200 (OK)
     */
    public void testGetNodeValid() throws Exception {
        // Creates a node
        Node node = new Node();
        node.setId(2L);
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
    @Tag("NodeController")
    /*
    Test GET a node with a non-existent id (id=1)
    Expected: 200 (OK)
     */
    public void testGetNodeNonExistent() throws Exception {
        mockMvc.perform(get("/node/1"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("NodeController")
    /*
    Test GET a node with non int id (id=q / id=2.2 / id=3.3)
    Expected: 400 (Bad Request)
     */
    public void testGetNodeNonIntId(String invalidId) throws Exception {
        mockMvc.perform(get("/node/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("NodeController")
    /*
    Test GET a node with no id in request
    Expected: 405 (Method Not Allowed)
     */
    public void testGetNodeNoIdInRequest() throws Exception {
        mockMvc.perform(get("/node"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("NodeController")
    /*
    Test GET all nodes
    Expected: 200 (OK)
     */
    public void testGetNodes() throws Exception {
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("NodeController")
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
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.isEmpty() || content.equals("[]"); // Check for an empty String or "[]"
                });
    }

    @Test
    @Tag("NodeController")
    /*
    Test UPDATE an existing node
    Expected: 200 (OK)
     */
    public void testUpdateNodeValid() throws Exception {
        // Add nodes via POST
        Node node = new Node();
        node.setId(2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());

        // Update the node
        node.setLongitude(2.3);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("NodeController")
    /*
    Test UPDATE a non-existant node
    Expected: 200 (OK) but no node is updated because the node does not exist
     */
    public void testUpdateNodeNonExistent() throws Exception {
        Node node = new Node();
        node.setId(1L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(put("/node/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("NodeController")
    /*
    Test UPDATE a node with a non int id in request (id=q / id=2.2 id=-)
    Expected: 400 (Bad Request)
     */
    public void testUpdateNodeNonIntId(String invalidId) throws Exception {
        Node node = new Node();
        node.setId(1L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(put("/node/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("NodeController")
    /*
    Test UPDATE a node with no id in request
    Expected: 405 (Method Not Allowed)
     */
    public void testUpdateNodeNoIdInRequest() throws Exception {
        Node node = new Node();
        node.setId(1L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(put("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("NodeController")
    /*
    Test UPDATE a node where id in request is different to id in body
    Expected: 200 (OK) because the id in the body is not taken into account
     */
    public void testUpdateNodeDifferentIdInBody() throws Exception {
        Node node = new Node();
        node.setId(1L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("NodeController")
    /*
    Test UPDATE incomplete node (id=2, latitude=null, longitude=null)
    Expected: 200 (OK) only parts of the node that are not null are updated
     */
    public void testUpdateNodeIncompleteNode() throws Exception {
        // Add node via POST\
        Node node = new Node();
        node.setId(2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());

        // Update the node
        node.setLatitude(19.1);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("NodeController")
    /*
    Test UPDATE node with no content in the body of the request
    Expected: 400 (Bad Request)
     */
    public void testUpdateNodeEmptyBody() throws Exception {
        mockMvc.perform(put("/node/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("NodeController")
    /*
    Test DELETE a valid node with an existing id (id=2)
    Expected: 200 (OK)
     */
    public void testDeleteNodeValid() throws Exception {
        // Creates a node
        Node node = new Node();
        node.setId(2L);
        node.setLongitude(2.2);
        node.setLatitude(3.3);
        mockMvc.perform(post("/node")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(node)));

        // Deletes the node
        mockMvc.perform(delete("/node/2"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("NodeController")
    /*
    Test DELETE a node with a non-existent id (id=1)
    Expected: 200 (OK) but no node is deleted because the node does not exist
     */
    public void testDeleteNodeNonExistent() throws Exception {
        mockMvc.perform(delete("/node/1"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("NodeController")
    /*
    Test DELETE a node with a non int id (q, 2.2, -)
    Expected: 400 (Bad Request)
     */
    public void testDeleteNonIntId(String invalidId) throws Exception {
        mockMvc.perform(delete("/node/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("NodeController")
    /*
    Test DELETE a node with no id
    Expected: 405 (Method Not Allowed)
     */
    public void testDeleteNodeNoIdInRequest() throws Exception {
        mockMvc.perform(delete("/node"))
                .andExpect(status().isMethodNotAllowed());
    }


    @Test
    @Tag("SegmentController")
    /*
    Test CREATE valid segment (origin=2, destination=3, length=0.5, name="segment")
    Expected: 200 (OK)
     */
    public void testCreateSegmentValid() throws Exception {
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(3.3);
        segment.setName("segment");
        mockMvc.perform(post("/segment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(segment)));
    }

    @Test
    @Tag("SegmentController")
    /*
    Test CREATE empty segment (idOrigin=null, idDestination=null, length=null, name=null)
    Expected: 200 (OK) because the segment is created even if it is incomplete, checks should be done before calling the controller
     */
    public void testCreateSegmentIncomplete() throws Exception {
        Segment segment = new Segment();
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test CREATE segment with an empty request body
    Expected: 400 (Bad Request)
     */
    public void testCreateSegmentEmptyBody() throws Exception {
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test GET a valid segment with an existing id (id=2)
    Expected: 200 (OK)
     */
    public void testGetSegmentValid() throws Exception {
        // Creates a segment
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(3.3);
        segment.setName("segment");
        mockMvc.perform(post("/segment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(segment)));

        // Gets the segment
        mockMvc.perform(get("/segment/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test GET a segment with a non-existent id (id=1)
    Expected: 200 (OK)
     */
    public void testGetSegmentNonExistent() throws Exception {
        mockMvc.perform(get("/segment/1"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("SegmentController")
    /*
    Test GET a segment with non int id (id=q / id=2.2 / id=3.3)
    Expected: 400 (Bad Request)
     */
    public void testGetSegmentNonIntId(String invalidId) throws Exception {
        mockMvc.perform(get("/segment/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test GET a segment with no id in request
    Expected: 405 (Method Not Allowed)
     */
    public void testGetSegmentNoIdInRequest() throws Exception {
        mockMvc.perform(get("/segment"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test GET all segments
    Expected: 200 (OK)
     */
    public void testGetSegments() throws Exception {
        mockMvc.perform(get("/segments"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test DELETE all segments
    Expected: 200 (OK) and an empty list of segments
     */
    public void testDeleteSegments() throws Exception {
        // Step 1: Create a couple of segments
        Segment segment1 = new Segment();
        segment1.setIdOrigin(3L);
        segment1.setIdDestination(2L);
        segment1.setLength(3.3);
        segment1.setName("segment1");

        Segment segment2 = new Segment();
        segment2.setIdOrigin(2L);
        segment2.setIdDestination(3L);
        segment2.setLength(3.3);
        segment2.setName("segment2");

        // Add segments via POST
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment2)))
                .andExpect(status().isOk());

        // Step 2: Verify the segments exist
        mockMvc.perform(get("/segments"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("\"idOrigin\":3,\"idDestination\":2");
                    assert content.contains("\"idOrigin\":2,\"idDestination\":3");
                });

        // Step 3: Delete all segments
        mockMvc.perform(delete("/segments"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of segments is empty
        mockMvc.perform(get("/segments"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.isEmpty() || content.equals("[]"); // Check for an empty String or "[]"
                });
    }

    @Test
    @Tag("SegmentController")
    /*
    Test UPDATE an existing segment
    Expected: 200 (OK)
     */
    public void testUpdateSegmentValid() throws Exception {
        // Add segments via POST
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(0.5);
        segment.setName("segment");
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());

        // Update the segment
        segment.setLength(1.5);
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test UPDATE a non-existent segment
    Expected: 200 (OK) but no segment is updated because the segment does not exist
     */
    public void testUpdateSegmentNonExistent() throws Exception {
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(0.5);
        segment.setName("segment");
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("SegmentController")
    /*
    Test UPDATE a segment with a non int id in request (id=q / id=2.2 id=-)
    Expected: 400 (Bad Request)
     */
    public void testUpdateSegmentNonIntId(String invalidId) throws Exception {
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(0.5);
        segment.setName("segment");
        mockMvc.perform(put("/segment/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test UPDATE a segment with no id in request
    Expected: 405 (Method Not Allowed)
     */
    public void testUpdateSegmentNoIdInRequest() throws Exception {
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(0.5);
        segment.setName("segment");
        mockMvc.perform(put("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test UPDATE segment with empty segment object
    Expected: 200 (OK) because the parameter is not updated if it is null
     */
    public void testUpdateSegmentEmptySegment() throws Exception {
        Segment segment = new Segment();
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test UPDATE a segment with no content in the body of the request
    Expected: 400 (Bad Request)
     */
    public void testUpdateSegmentEmptyBody() throws Exception {
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test UPDATE incomplete segment (idOrigin=3, idDestination=2, length=null, name=null)
    Expected: 200 (OK) only parts of the segment that are not null are updated
     */
    public void testUpdateSegmentIncompleteSegment() throws Exception {
        // Add segment via POST
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idOrigin\":3,\"idDestination\":2,\"length\":0.5,\"name\":\"segment\"}"))
                .andExpect(status().isOk());
        // Update the segment
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idOrigin\":4,\"idDestination\":4,\"length\":4,\"name\":\"another\"}"))
                .andExpect(status().isOk());
    }


    @Test
    @Tag("SegmentController")
    /*
    Test DELETE a valid segment with an existing id (id=1)
    Expected: 200 (OK)
     */
    public void testDeleteSegmentValid() throws Exception {
        // Creates a segment
        Segment segment = new Segment();
        segment.setIdOrigin(3L);
        segment.setIdDestination(2L);
        segment.setLength(0.5);
        segment.setName("segment");
        mockMvc.perform(post("/segment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(segment)));

        // Deletes the segment
        mockMvc.perform(delete("/segment/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test DELETE a segment with a non-existent id (id=1)
    Expected: 200 (OK) but no segment is deleted because the segment does not exist
     */
    public void testDeleteSegmentNonExistent() throws Exception {
        mockMvc.perform(delete("/segment/1"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("SegmentController")
    /*
    Test DELETE a segment with a non int id (id=q / id=2.2 / id=3.3)
    Expected: 400 (Bad Request)
     */
    public void testDeleteSegmentInvalidId(String invalidId) throws Exception {
        mockMvc.perform(delete("/segment/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("SegmentController")
    /*
    Test DELETE a segment with no id
    Expected: 405 (Method Not Allowed)
     */
    public void testDeleteSegmentNoIdInRequest() throws Exception {
        mockMvc.perform(delete("/segment"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test CREATE valid delivery request (idPickup=2, idDelivery=3, idWarehouse=4, idCourier=5)
    Expected: 200 (OK)
     */
    public void testCreateDeliveryRequestValid() throws Exception {
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":2,\"idDelivery\":3,\"idWarehouse\":4,\"idCourier\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test CREATE delivery request with an empty request body
    Expected: 400 (Bad Request)
     */
    public void testCreateDeliveryRequestEmptyBody() throws Exception {
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test CREATE incomplete delivery request (idDelivery=3)
    Expected: 200 (OK) because the delivery request is created even if it is incomplete, checks should be made before calling the controller
     */
    public void testCreateDeliveryRequestIncomplete() throws Exception {
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idDelivery\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test GET valid delivery request with an existing id (id=1)
    Expected: 200 (OK)
     */
    public void testGetDeliveryRequestValid() throws Exception {
        // Creates a delivery request
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":2,\"idDelivery\":3,\"idWarehouse\":4,\"idCourier\":5}"))
                .andExpect(status().isOk());

        // Gets the delivery request
        mockMvc.perform(get("/delivery_request/1"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test GET a delivery request with a non-existent id (id=1)
    Expected: 200 (OK)
     */
    public void testGetDeliveryRequestNonExistent() throws Exception {
        mockMvc.perform(get("/delivery_request/1"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @Tag("DeliveryRequestController")
    @ValueSource(strings = {"q", "2.2", "-"})
    /*
    Test GET a delivery request with non int id (id=q / id=2.2 / id=3.3)
    Expected: 400 (Bad Request)
     */
    public void testGetDeliveryRequestInvalidId(String invalidId) throws Exception {
        mockMvc.perform(get("/delivery_request/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test GET a delivery request with no id in request
    Expected: 405 (Method Not Allowed)
     */
    public void testGetDeliveryRequestNoIdInRequest() throws Exception {
        mockMvc.perform(get("/delivery_request"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test GET all delivery requests
    Expected: 200 (OK)
     */
    public void testGetDeliveryRequests() throws Exception {
        mockMvc.perform(get("/delivery_requests"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test DELETE all delivery requests
    Expected: 200 (OK) and an empty list of delivery requests
     */
    public void testDeleteDeliveryRequests() throws Exception {
        // Step 1: Create a couple of delivery requests
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":2,\"idDelivery\":3,\"idWarehouse\":4,\"idCourier\":5}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":3,\"idDelivery\":4,\"idWarehouse\":5,\"idCourier\":6}"))
                .andExpect(status().isOk());

        // Step 2: Verify the delivery requests exist
        mockMvc.perform(get("/delivery_requests"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.contains("\"idPickup\":2,\"idDelivery\":3,\"idWarehouse\":4,\"idCourier\":5");
                    assert content.contains("\"idPickup\":3,\"idDelivery\":4,\"idWarehouse\":5,\"idCourier\":6");
                });

        // Step 3: Delete all delivery requests
        mockMvc.perform(delete("/delivery_requests"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of delivery requests is empty
        mockMvc.perform(get("/delivery_requests"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert content.isEmpty() || content.equals("[]"); // Check for an empty String or "[]"
                });
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test UPDATE an existing delivery request
    Expected: 200 (OK)
     */
    public void testUpdateDeliveryRequestValid() throws Exception {
        // Add delivery requests via POST
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":2,\"idDelivery\":3,\"idWarehouse\":4,\"idCourier\":5}"))
                .andExpect(status().isOk());

        // Update the delivery request
        mockMvc.perform(put("/delivery_request/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":3,\"idDelivery\":4,\"idWarehouse\":5,\"idCourier\":6}"))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test UPDATE a non-existant delivery request
    Expected: 200 (OK) but no delivery request is updated because the delivery request does not exist
     */
    public void testUpdateDeliveryRequestNonExistent() throws Exception {
        mockMvc.perform(put("/delivery_request/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":3,\"idDelivery\":4,\"idWarehouse\":5,\"idCourier\":6}"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("DeliveryRequestController")
    /*
    Test UPDATE a delivery request with a non int id in request (id=q / id=2.2 id=-)
    Expected: 400 (Bad Request)
     */
    public void testUpdateDeliveryRequestInvalidId(String invalidId) throws Exception {
        mockMvc.perform(put("/delivery_request/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":3,\"idDelivery\":4,\"idWarehouse\":5,\"idCourier\":6}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test UPDATE a delivery request with no id in request
    Expected: 405 (Method Not Allowed)
     */
    public void testUpdateDeliveryRequestNoIdInRequest() throws Exception {
        mockMvc.perform(put("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPickup\":3,\"idDelivery\":4,\"idWarehouse\":5,\"idCourier\":6}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test UPDATE a delivery request with empty delivery request object
    Expected: 200 (OK) because the parameter is not updated if it is null
     */
    public void testUpdateDeliveryRequestEmptyDeliveryRequest() throws Exception {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        mockMvc.perform(put("/delivery_request/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test UPDATE a delivery request with no content in the body of the request
    Expected: 400 (Bad Request)
     */
    public void testUpdateDeliveryRequestEmptyBody() throws Exception {
        mockMvc.perform(put("/delivery_request/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Tag("DeliveryRequestController")
    /*
    Test DELETE a valid delivery request with an existing id (id=1)
    Expected: 200 (OK)
     */
    public void testDeleteDeliveryRequestValid() throws Exception {
        // Creates a delivery request
        mockMvc.perform(post("/delivery_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idPickup\":2,\"idDelivery\":3,\"idWarehouse\":4,\"idCourier\":5}"));

        // Deletes the delivery request
        mockMvc.perform(delete("/delivery_request/1"))
                .andExpect(status().isOk());
    }
}

