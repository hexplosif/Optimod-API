package com.hexplosif.OptimodBackEnd;

import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import com.hexplosif.OptimodBackEnd.model.Node;
import com.hexplosif.OptimodBackEnd.model.Segment;
import com.hexplosif.OptimodBackEnd.service.OptimodService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class OptimodServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OptimodService optimodService;

    @BeforeEach
    @AfterEach
    public void setUp() {
        // Clean up the database before and after each test
        optimodService.deleteAllNodes();
        optimodService.deleteAllSegments();
        optimodService.deleteAllDeliveryRequests();
    }

    @Test
    public void testCorrectLoadNode() throws Exception {
        optimodService.loadNode("src/test/java/testResources/petitPlan.xml");

        // Verify that nodes are loaded correctly
        Optional<Node> node = optimodService.findNodeById(25175791L);
        assertTrue("The node doesn't exist", node.isPresent());
        assertEquals("The ID is incorrect",25175791L, node.get().getId());
        assertEquals("The Latitude is incorrect",45.75406, node.get().getLatitude());
        assertEquals("The Longitude is incorrect",4.857418, node.get().getLongitude());
    }

    @Test
    public void testCorrectLoadSegment() throws Exception {
        optimodService.loadSegment("src/test/java/testResources/petitPlan.xml");

        // Verify that segments are loaded correctly
        Optional<Segment> segment = optimodService.findSegmentById(1L);
        assertTrue("The segment doesn't exist", segment.isPresent());
        assertEquals("The destination is incorrect",25175778L, segment.get().getIdDestination());
        assertEquals("The longueur is incorrect",69.979805, segment.get().getLength());
        assertEquals("The nomRue is incorrect","Rue Danton", segment.get().getName());
        assertEquals("The origine is incorrect",25175791L, segment.get().getIdOrigin());
    }

    @Test
    public void testCorrectLoadDeliveryRequest() throws Exception {
        optimodService.loadDeliveryRequest("src/test/java/testResources/demandePetit1.xml");

        // Verify that delivery requests are loaded correctly
        Optional<DeliveryRequest> deliveryRequest = optimodService.findDeliveryRequestById(1L);
        System.out.println(deliveryRequest);
        assertTrue("The delivery request doesn't exist", deliveryRequest.isPresent());
        assertEquals("The delivery request warehouse is incorrect",342873658L, deliveryRequest.get().getIdWarehouse());
        assertEquals("The delivery request pickup address is incorrect",208769039L, deliveryRequest.get().getIdPickup());
        assertEquals("The delivery request delivery address is incorrect",25173820L, deliveryRequest.get().getIdDelivery());
    }
}

