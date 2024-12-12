package com.hexplosif.OptimodBackEnd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexplosif.OptimodBackEnd.model.Courier;
import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import com.hexplosif.OptimodBackEnd.model.Node;
import com.hexplosif.OptimodBackEnd.model.Segment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OptimodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc.perform(delete("/nodes"));
        mockMvc.perform(delete("/segments"));
        mockMvc.perform(delete("/delivery_requests"));
        mockMvc.perform(delete("/couriers"));
    }

    /**
     * Helper method to create a node
     *
     * @param id        the id of the node
     * @param latitude  the latitude of the node
     * @param longitude the longitude of the node
     * @return the node
     */
    private Node setNode(Long id, Double latitude, Double longitude) {
        Node node = new Node();
        node.setId(id);
        node.setLatitude(latitude);
        node.setLongitude(longitude);
        return node;
    }

    /**
     * Helper method to create a segment
     *
     * @param idOrigin      the id of the origin node
     * @param idDestination the id of the destination node
     * @param length        the length of the segment
     * @param name          the name of the segment
     * @return the segment
     */
    private Segment setSegment(Long idOrigin, Long idDestination, Double length, String name) {
        Segment segment = new Segment();
        segment.setIdOrigin(idOrigin);
        segment.setIdDestination(idDestination);
        segment.setLength(length);
        segment.setName(name);
        return segment;
    }

    /**
     * Helper method to create a delivery request
     *
     * @param idPickup    the id of the pickup node
     * @param idDelivery  the id of the delivery node
     * @param idWarehouse the id of the warehouse node
     * @param idCourier   the id of the courier node
     * @return the delivery request
     */
    private DeliveryRequest setDeliveryRequest(Long idPickup, Long idDelivery, Long idWarehouse, Long idCourier) {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setIdPickup(idPickup);
        deliveryRequest.setIdDelivery(idDelivery);
        deliveryRequest.setIdWarehouse(idWarehouse);
        deliveryRequest.setIdCourier(idCourier);
        return deliveryRequest;
    }

    /**
     * Helper method to create a courier
     *
     * @param name the name of the courier
     */
    private Courier setCourier(String name) {
        Courier courier = new Courier();
        courier.setName(name);
        return courier;
    }


    /**
     * Test CREATE a valid node (id=2, latitude=3.3, longitude=2.2)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testCreateNodeValid() throws Exception {
        Node node = setNode(2L, 3.3, 2.2);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.latitude").value(3.3))
                .andExpect(jsonPath("$.longitude").value(2.2));
    }

    /**
     * Test CREATE multiple nodes at the same time
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testCreateNodesValid() throws Exception {
        Node node1 = setNode(2L, 3.3, 2.2);
        Node node2 = setNode(3L, 3.1, 2.1);
        mockMvc.perform(post("/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Node[]{node1, node2})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * Test CREATE incomplete node (id=2, latitude=null, longitude=null)
     * Expected: 200 (OK) because the node is created even if it is incomplete, checks should be made before calling the controller
     */
    @Test
    @Tag("NodeController")
    public void testCreateNodeIncomplete() throws Exception {
        Node node = setNode(2L, null, null);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }


    /**
     * Test CREATE node with an empty request body
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("NodeController")
    public void testCreateNodeEmptyBody() throws Exception {
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test CREATE node with a negative id (id=-2)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testCreateNodeNegativeId() throws Exception {
        Node node = setNode(-2L, 3.3, 2.2);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(-2L))
                .andExpect(jsonPath("$.latitude").value(3.3))
                .andExpect(jsonPath("$.longitude").value(2.2));

    }


    /**
     * Test CREATE node with a non int id (id=q)
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("NodeController")
    public void testGetNodeValid() throws Exception {
        // Creates a node
        Node node = setNode(2L, 3.3, 2.2);
        mockMvc.perform(post("/node")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(node)));

        // Gets the node
        mockMvc.perform(get("/node/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.latitude").value(3.3))
                .andExpect(jsonPath("$.longitude").value(2.2));
    }

    /**
     * Test GET a node with a non-existent id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testGetNodeNonExistent() throws Exception {
        mockMvc.perform(get("/node/99"))
                .andExpect(status().isOk());
    }

    /**
     * Test GET a node with non int id (id=q / id=2.2 / id=3.3)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("NodeController")
    public void testGetNodeNonIntId(String invalidId) throws Exception {
        mockMvc.perform(get("/node/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test GET a node with no id in request
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("NodeController")
    public void testGetNodeNoIdInRequest() throws Exception {
        mockMvc.perform(get("/node"))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test GET all nodes
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testGetNodes() throws Exception {
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk());
    }


    /**
     * Test DELETE all nodes
     * Expected: 200 (OK) and an empty list of nodes
     */
    @Test
    @Tag("NodeController")
    public void testDeleteNodes() throws Exception {
        // Step 1: Create and add nodes
        Node node1 = setNode(2L, 3.3, 2.2);
        Node node2 = setNode(3L, 3.1, 2.1);
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
                .andExpect(jsonPath("$.length()").value(2)) // Verify two nodes exist
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));

        // Step 3: Delete all nodes
        mockMvc.perform(delete("/nodes"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of nodes is empty
        mockMvc.perform(get("/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Explicitly check the list is empty
    }

    /**
     * Test UPDATE an existing node
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testUpdateNodeValid() throws Exception {
        // Create and add node
        Node node = setNode(2L, 3.3, 2.2);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.latitude").value(3.3))
                .andExpect(jsonPath("$.longitude").value(2.2));

        // Update the node
        node.setLongitude(2.3);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/node/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.latitude").value(3.3))
                .andExpect(jsonPath("$.longitude").value(2.3));
    }

    /**
     * Test UPDATE a non-existant node
     * Expected: 200 (OK) but no node is updated because the node does not exist
     */
    @Test
    @Tag("NodeController")
    public void testUpdateNodeNonExistent() throws Exception {
        Node node = setNode(2L, 3.3, 2.2);
        mockMvc.perform(put("/node/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // Explicitly check the list is empty
    }

    /**
     * Test UPDATE a node with a non int id in request (id=q / id=2.2 id=-)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("NodeController")
    public void testUpdateNodeNonIntId(String invalidId) throws Exception {
        Node node = setNode(1L, 3.3, 2.2);
        mockMvc.perform(put("/node/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test UPDATE a node with no id in request
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("NodeController")
    public void testUpdateNodeNoIdInRequest() throws Exception {
        Node node = setNode(1L, 3.3, 2.2);
        mockMvc.perform(put("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test UPDATE a node where id in request is different to id in body
     * Expected: 200 (OK) because the id in the body is not taken into account
     */
    @Test
    @Tag("NodeController")
    public void testUpdateNodeDifferentIdInBody() throws Exception {
        Node node = setNode(1L, 3.3, 2.2);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());
    }

    /**
     * Test UPDATE incomplete node (id=null, latitude=19.1, longitude=null)
     * Expected: 200 (OK) only parts of the node that are not null are updated
     */
    @Test
    @Tag("NodeController")
    public void testUpdateNodeIncompleteNode() throws Exception {
        // Create and add node
        Node node1 = setNode(2L, 3.3, 2.2);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node1)))
                .andExpect(status().isOk());

        // Update the node
        Node node2 = setNode(null, 19.1, null);
        mockMvc.perform(put("/node/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.latitude").value(19.1))
                .andExpect(jsonPath("$.longitude").value(2.2));
    }

    /**
     * Test UPDATE node with no content in the body of the request
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("NodeController")
    public void testUpdateNodeEmptyBody() throws Exception {
        mockMvc.perform(put("/node/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test DELETE a valid node with an existing id (id=2)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("NodeController")
    public void testDeleteNodeValid() throws Exception {
        // Creates and adds a node
        Node node = setNode(2L, 3.3, 2.2);
        mockMvc.perform(post("/node")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(node)))
                .andExpect(status().isOk());

        // Deletes the node
        mockMvc.perform(delete("/node/2"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE a node with a non-existent id (id=1)
     * Expected: 200 (OK) but no node is deleted because the node does not exist
     */
    @Test
    @Tag("NodeController")
    public void testDeleteNodeNonExistent() throws Exception {
        mockMvc.perform(delete("/node/99"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE a node with a non int id (q, 2.2, -)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("NodeController")
    public void testDeleteNonIntId(String invalidId) throws Exception {
        mockMvc.perform(delete("/node/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test DELETE a node with no id
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("NodeController")
    public void testDeleteNodeNoIdInRequest() throws Exception {
        mockMvc.perform(delete("/node"))
                .andExpect(status().isMethodNotAllowed());
    }


    /**
     * Test CREATE valid segment (origin=2, destination=3, length=0.5, name="segment")
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testCreateSegmentValid() throws Exception {
        Segment segment = setSegment(2L, 3L, 0.5, "segment");
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    /**
     * Test CREATE multiple segments at the same time
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testCreateSegmentsValid() throws Exception {
        Segment segment1 = setSegment(2L, 3L, 0.5, "segment1");
        Segment segment2 = setSegment(3L, 4L, 0.5, "segment2");
        mockMvc.perform(post("/segments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Segment[]{segment1, segment2})))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * Test CREATE empty segment (idOrigin=null, idDestination=null, length=null, name=null)
     * Expected: 200 (OK) because the segment is created even if it is incomplete, checks should be done before calling the controller
     */
    @Test
    @Tag("SegmentController")
    public void testCreateSegmentIncomplete() throws Exception {
        Segment segment = new Segment();
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    /**
     * Test CREATE segment with an empty request body
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("SegmentController")
    public void testCreateSegmentEmptyBody() throws Exception {
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test GET a valid segment with an existing id (id=2)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testGetSegmentValid() throws Exception {
        // Creates and adds a segment
        Segment segment = setSegment(2L, 3L, 0.5, "segment");
        mockMvc.perform(post("/segment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(segment)));

        // Gets the segment
        mockMvc.perform(get("/segment/1"))
                .andExpect(status().isOk());
    }

    /**
     * Test GET a segment with a non-existent id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testGetSegmentNonExistent() throws Exception {
        mockMvc.perform(get("/segment/99"))
                .andExpect(status().isOk());
    }

    /**
     * Test GET a segment with non int id (id=q / id=2.2 / id=3.3)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("SegmentController")
    public void testGetSegmentNonIntId(String invalidId) throws Exception {
        mockMvc.perform(get("/segment/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test GET a segment with no id in request
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("SegmentController")
    public void testGetSegmentNoIdInRequest() throws Exception {
        mockMvc.perform(get("/segment"))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test GET all segments
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testGetSegments() throws Exception {
        mockMvc.perform(get("/segments"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE all segments
     * Expected: 200 (OK) and an empty list of segments
     */
    @Test
    @Tag("SegmentController")
    public void testDeleteSegments() throws Exception {
        // Step 1: Create and adds segments
        Segment segment1 = setSegment(3L, 3L, 0.5, "segment1");
        Segment segment2 = setSegment(2L, 3L, 3.5, "segment2");
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
                .andExpect(jsonPath("$.length()").value(2)); // Verify two segments exist

        // Step 3: Delete all segments
        mockMvc.perform(delete("/segments"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of segments is empty
        mockMvc.perform(get("/segments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Explicitly check the list is empty
    }

    /**
     * Test UPDATE an existing segment
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testUpdateSegmentValid() throws Exception {
        // Step 1: Create and add the segment via POST and capture the response
        Segment segment = setSegment(2L, 3L, 0.5, "segment");
        MvcResult postResult = mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk())
                .andReturn();

        // Step 2: Parse the response to get the ID of the created segment
        String postResponseContent = postResult.getResponse().getContentAsString();
        Long createdId = objectMapper.readValue(postResponseContent, Segment.class).getId();

        // Step 3: Update the segment with the new length value
        segment.setLength(1.5);
        mockMvc.perform(put("/segment/" + createdId) // Use the dynamically captured ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId)) // Assert that the ID is correct
                .andExpect(jsonPath("$.length").value(1.5)); // Assert that the length is updated correctly
    }


    /**
     * Test UPDATE a non-existent segment
     * Expected: 200 (OK) but no segment is updated because the segment does not exist
     */
    @Test
    @Tag("SegmentController")
    public void testUpdateSegmentNonExistent() throws Exception {
        Segment segment = setSegment(2L, 3L, 0.5, "segment");
        mockMvc.perform(put("/segment/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // Explicitly check the list is empty
    }

    /**
     * Test UPDATE a segment with a non int id in request (id=q / id=2.2 id=-)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("SegmentController")
    public void testUpdateSegmentNonIntId(String invalidId) throws Exception {
        Segment segment = setSegment(3L, 2L, 0.5, "segment");
        mockMvc.perform(put("/segment/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test UPDATE a segment with no id in request
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("SegmentController")
    public void testUpdateSegmentNoIdInRequest() throws Exception {
        Segment segment = setSegment(3L, 2L, 0.5, "segment");
        mockMvc.perform(put("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test UPDATE segment with empty segment object
     * Expected: 200 (OK) because the parameter is not updated if it is null
     */
    @Test
    @Tag("SegmentController")
    public void testUpdateSegmentEmptySegment() throws Exception {
        Segment segment = new Segment();
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());
    }

    /**
     * Test UPDATE a segment with no content in the body of the request
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("SegmentController")
    public void testUpdateSegmentEmptyBody() throws Exception {
        mockMvc.perform(put("/segment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test UPDATE incomplete segment (idOrigin=null, idDestination=null, length=null, name=null)
     * Expected: 200 (OK) only parts of the segment that are not null are updated
     */
    @Test
    @Tag("SegmentController")
    public void testUpdateSegmentIncompleteSegment() throws Exception {
        // Step 1: Create and add the segment via POST and capture the response
        Segment segment = setSegment(3L, 2L, 0.5, "segment");
        MvcResult postResult = mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk())
                .andReturn();

        // Step 2: Parse the response to get the ID of the created segment
        String postResponseContent = postResult.getResponse().getContentAsString();
        Long createdId = objectMapper.readValue(postResponseContent, Segment.class).getId();

        // Step 3: Update the segment with incomplete data
        Segment segment2 = setSegment(null, null, 1.5, null); // assuming these null values are valid
        mockMvc.perform(put("/segment/" + createdId) // Use the dynamically captured ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId)) // Assert that the ID is correct
                .andExpect(jsonPath("$.length").value(1.5)); // Assert that the length is updated
    }


    /**
     * Test DELETE a valid segment with an existing id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("SegmentController")
    public void testDeleteSegmentValid() throws Exception {
        // Creates and add a segment
        Segment segment = setSegment(2L, 3L, 0.5, "segment");
        mockMvc.perform(post("/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segment)))
                .andExpect(status().isOk());

        // Deletes the segment
        mockMvc.perform(delete("/segment/1"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE a segment with a non-existent id (id=1)
     * Expected: 200 (OK) but no segment is deleted because the segment does not exist
     */
    @Test
    @Tag("SegmentController")
    public void testDeleteSegmentNonExistent() throws Exception {
        mockMvc.perform(delete("/segment/99"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE a segment with a non int id (id=q / id=2.2 / id=3.3)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("SegmentController")
    public void testDeleteSegmentInvalidId(String invalidId) throws Exception {
        mockMvc.perform(delete("/segment/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test DELETE a segment with no id
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("SegmentController")
    public void testDeleteSegmentNoIdInRequest() throws Exception {
        mockMvc.perform(delete("/segment"))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test CREATE valid delivery request (idPickup=2, idDelivery=3, idWarehouse=4, idCourier=5)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testCreateDeliveryRequestValid() throws Exception {
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test CREATE delivery request with an empty request body
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testCreateDeliveryRequestEmptyBody() throws Exception {
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

    }

    /**
     * Test CREATE incomplete delivery request (idDelivery=3)
     * Expected: 200 (OK) because the delivery request is created even if it is incomplete, checks should be made before calling the controller
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testCreateDeliveryRequestIncomplete() throws Exception {
        DeliveryRequest deliveryRequest = setDeliveryRequest(null, 3L, null, null);
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test GET valid delivery request with an existing id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testGetDeliveryRequestValid() throws Exception {
        // Creates a delivery request
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());

        // Gets the delivery request
        mockMvc.perform(get("/delivery_request/1"))
                .andExpect(status().isOk());
    }

    /**
     * Test GET a delivery request with a non-existent id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testGetDeliveryRequestNonExistent() throws Exception {
        mockMvc.perform(get("/delivery_request/99"))
                .andExpect(status().isOk());
    }

    /**
     * Test GET a delivery request with non int id (id=q / id=2.2 / id=3.3)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @Tag("DeliveryRequestController")
    @ValueSource(strings = {"q", "2.2", "-"})
    public void testGetDeliveryRequestInvalidId(String invalidId) throws Exception {
        mockMvc.perform(get("/delivery_request/" + invalidId))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test GET a delivery request with no id in request
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testGetDeliveryRequestNoIdInRequest() throws Exception {
        mockMvc.perform(get("/delivery_request"))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test GET all delivery requests
     * Expected: 200 (OK)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testGetDeliveryRequests() throws Exception {
        mockMvc.perform(get("/delivery_requests"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE all delivery requests
     * Expected: 200 (OK) and an empty list of delivery requests
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testDeleteDeliveryRequests() throws Exception {
        // Step 1: Create a couple of delivery requests
        DeliveryRequest deliveryRequest1 = setDeliveryRequest(2L, 3L, 4L, 5L);
        DeliveryRequest deliveryRequest2 = setDeliveryRequest(3L, 4L, 5L, 6L);
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest2)))
                .andExpect(status().isOk());

        // Step 2: Verify the delivery requests exist
        mockMvc.perform(get("/delivery_requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // Verify two delivery requests exist

        // Step 3: Delete all delivery requests
        mockMvc.perform(delete("/delivery_requests"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of delivery requests is empty
        mockMvc.perform(get("/delivery_requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Explicitly check the list is empty
    }

    /**
     * Test UPDATE an existing delivery request
     * Expected: 200 (OK)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testUpdateDeliveryRequestValid() throws Exception {
        // Add a delivery request via POST and capture the response
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        MvcResult postResult = mockMvc.perform(post("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse the response to extract the ID
        String postResponseContent = postResult.getResponse().getContentAsString();
        Long createdId = objectMapper.readValue(postResponseContent, DeliveryRequest.class).getId();

        // Update the delivery request
        deliveryRequest.setIdPickup(3L);
        mockMvc.perform(put("/delivery_request/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());
    }


    /**
     * Test UPDATE a non-existant delivery request
     * Expected: 200 (OK) but no delivery request is updated because the delivery request does not exist
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testUpdateDeliveryRequestNonExistent() throws Exception {
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        mockMvc.perform(put("/delivery_request/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test UPDATE a delivery request with a non int id in request (id=q / id=2.2 id=-)
     * Expected: 400 (Bad Request)
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "2.2", "-"})
    @Tag("DeliveryRequestController")
    public void testUpdateDeliveryRequestInvalidId(String invalidId) throws Exception {
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        mockMvc.perform(put("/delivery_request/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test UPDATE a delivery request with no id in request
     * Expected: 405 (Method Not Allowed)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testUpdateDeliveryRequestNoIdInRequest() throws Exception {
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        mockMvc.perform(put("/delivery_request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test UPDATE a delivery request with empty delivery request object
     * Expected: 200 (OK) because the parameter is not updated if it is null
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testUpdateDeliveryRequestEmptyDeliveryRequest() throws Exception {
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        mockMvc.perform(put("/delivery_request/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test UPDATE a delivery request with no content in the body of the request
     * Expected: 400 (Bad Request)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testUpdateDeliveryRequestEmptyBody() throws Exception {
        mockMvc.perform(put("/delivery_request/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test DELETE a valid delivery request with an existing id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testDeleteDeliveryRequestValid() throws Exception {
        // Creates a delivery request
        DeliveryRequest deliveryRequest = setDeliveryRequest(2L, 3L, 4L, 5L);
        mockMvc.perform(post("/delivery_request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliveryRequest)));

        // Deletes the delivery request
        mockMvc.perform(delete("/delivery_request/1"))
                .andExpect(status().isOk());
    }

    /**
     * Test CREATE valid courier (name="courier")
     * Expected: 200 (OK)
     */
    @Test
    @Tag("CourierController")
    public void testCreateCourierValid() throws Exception {
        Courier courier = setCourier("courier");
        mockMvc.perform(post("/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courier)))
                .andExpect(status().isOk());
    }

    /**
     * Test GET a valid courier with an existing id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("CourierController")
    public void testGetCourierValid() throws Exception {
        // Step 1: Create a courier via POST and capture the response
        Courier courier = setCourier("courier");
        MvcResult postResult = mockMvc.perform(post("/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courier)))
                .andExpect(status().isOk())
                .andReturn();

        // Step 2: Parse the response to get the ID of the created courier
        String postResponseContent = postResult.getResponse().getContentAsString();
        Long createdId = objectMapper.readValue(postResponseContent, Courier.class).getId();

        // Step 3: Get the courier using the captured ID
        mockMvc.perform(get("/courier/" + createdId)) // Use the dynamically captured ID
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId)); // Assert that the ID matches
    }


    /**
     * Test GET all couriers
     * Expected: 200 (OK)
     */
    @Test
    @Tag("CourierController")
    public void testGetCouriers() throws Exception {
        mockMvc.perform(get("/couriers"))
                .andExpect(status().isOk());
    }

    /**
     * Test DELETE all couriers
     * Expected: 200 (OK) and an empty list of couriers
     */
    @Test
    @Tag("CourierController")
    public void testDeleteCouriers() throws Exception {
        // Step 1: Create and add couriers
        Courier courier1 = setCourier("courier1");
        Courier courier2 = setCourier("courier2");
        mockMvc.perform(post("/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courier1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courier2)))
                .andExpect(status().isOk());

        // Step 2: Verify the couriers exist
        mockMvc.perform(get("/couriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // Verify two couriers exist

        // Step 3: Delete all couriers
        mockMvc.perform(delete("/couriers"))
                .andExpect(status().isOk());

        // Step 4: Verify that the list of couriers is empty
        mockMvc.perform(get("/couriers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // Explicitly check the list is empty
    }

    /**
     * Test DELETE a valid courier with an existing id (id=1)
     * Expected: 200 (OK)
     */
    @Test
    @Tag("CourierController")
    public void testDeleteCourierValid() throws Exception {
        // Add a courier via POST and capture the response
        Courier courier = setCourier("courier");
        MvcResult postResult = mockMvc.perform(post("/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courier)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse the response to extract the ID
        String postResponseContent = postResult.getResponse().getContentAsString();
        Long createdId = objectMapper.readValue(postResponseContent, Courier.class).getId();

        // Deletes the courier
        mockMvc.perform(delete("/courier/" + createdId))
                .andExpect(status().isOk());
    }

    /**
     * Test CREATE "addCourier" request
     * Expected: 200 (OK)
     */
    @Test
    @Tag("CourierController")
    public void testAddCourierFunction() throws Exception {
        mockMvc.perform(post("/addCourier"))
                .andExpect(status().isOk());
    }

    /**
     * Test CREATE "deleteCourier" request
     * Expected: 200 (OK)
     */
    @Test
    @Tag("CourierController")
    public void testDeleteCourierFunction() throws Exception {
        mockMvc.perform(post("/addCourier"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/deleteCourier"))
                .andExpect(status().isOk());
    }

    /**
     * Test POST a valid map file (petitPlan.xml)
     *
     * @throws Exception if the file is not found
     */
    @Test
    @Tag("MapController")
    public void testLoadMapValidFromFile2() throws Exception {
        // Step 1: Load the file from the classpath directly as a resource
        MockMultipartFile mockFile = loadMockMultipartFile("petitPlan.xml");

        // Step 2: Perform the file upload via POST
        MvcResult result = mockMvc.perform(multipart("/loadMap")
                        .file(mockFile)) // Attach the file
                .andExpect(status().isOk()) // Expect HTTP 200 (OK)
                .andExpect(jsonPath("$.nodes").isArray()) // Validate 'nodes' is an array
                .andExpect(jsonPath("$.segments").isArray()) // Validate 'segments' is an array
                .andReturn();

        // Step 3: Parse and verify the response content
        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent, "Response content should not be null.");

        // Parse the response into a map
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        // Assert that 'nodes' and 'segments' keys exist in the response
        assertNotNull(responseMap, "Response map should not be null.");
        assertTrue(responseMap.containsKey("nodes"), "Response should contain 'nodes' key.");
        assertTrue(responseMap.containsKey("segments"), "Response should contain 'segments' key.");
    }

    /**
     * Test POST a valid delivery request file (demandePetit1.xml)
     *
     * @throws Exception if the file is not found
     */
    @Test
    @Tag("DeliveryRequestController")
    public void testLoadDeliveryRequestValidFromFile() throws Exception {
        // Step 0: Load a map file first
        MockMultipartFile mockMapFile = loadMockMultipartFile("petitPlan.xml");
        mockMvc.perform(multipart("/loadMap")
                        .file(mockMapFile)) // Attach the file
                .andExpect(status().isOk()) // Expect HTTP 200 (OK)
                .andExpect(jsonPath("$.nodes").isArray()) // Validate 'nodes' is an array
                .andExpect(jsonPath("$.segments").isArray()); // Validate 'segments' is an array

        // Step 1: Load the file from the classpath directly as a resource
        MockMultipartFile mockDeliveryRequestFile = loadMockMultipartFile("demandePetit1.xml");

        // Step 2: Perform the file upload via POST
        MvcResult result = mockMvc.perform(multipart("/loadDeliveryRequest")
                        .file(mockDeliveryRequestFile)) // Attach the file
                .andExpect(status().isOk()) // Expect HTTP 200 (OK)
                .andExpect(jsonPath("$.deliveryRequests").isArray()) // Validate 'deliveryRequests' is an array
                .andReturn();

        // Step 3: Parse and verify the response content
        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent, "Response content should not be null.");

        // Parse the response into a map
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);

        // Assert that 'deliveryRequests' key exists in the response
        assertNotNull(responseMap, "Response map should not be null.");
        assertTrue(responseMap.containsKey("deliveryRequests"), "Response should contain 'deliveryRequests' key.");
    }

    private static MockMultipartFile loadMockMultipartFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        assert resource.exists() : "Resource should exist.";

        // Create a MockMultipartFile using the resource
        return new MockMultipartFile(
                "file", // Form parameter name
                resource.getFilename(), // Original filename
                MediaType.APPLICATION_XML_VALUE, // Content type
                resource.getInputStream() // File content as InputStream
        );
    }

}

