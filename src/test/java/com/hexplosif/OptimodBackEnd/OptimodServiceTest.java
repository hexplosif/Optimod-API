package com.hexplosif.OptimodBackEnd;

import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import com.hexplosif.OptimodBackEnd.model.Node;
import com.hexplosif.OptimodBackEnd.model.Segment;
import com.hexplosif.OptimodBackEnd.service.OptimodService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Test class for OptimodService.
 * This class contains unit tests for the OptimodService class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OptimodServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OptimodService optimodService;

    /**
     * Sets up the test environment by cleaning up the database before and after each test.
     */
    @BeforeEach
    @AfterEach
    public void setUp() {
        optimodService.deleteAllNodes();
        optimodService.deleteAllSegments();
        optimodService.deleteAllDeliveryRequests();
    }

    /**
     * Tests that nodes are loaded correctly from an XML file.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("Load")
    public void testCorrectLoadNode() throws Exception {
        optimodService.loadNode("src/test/java/data/petitPlan.xml");

        Optional<Node> node = optimodService.findNodeById(25175791L);
        assertTrue("The node doesn't exist", node.isPresent());
        assertEquals("The ID is incorrect", 25175791L, node.get().getId());
        assertEquals("The Latitude is incorrect", 45.75406, node.get().getLatitude());
        assertEquals("The Longitude is incorrect", 4.857418, node.get().getLongitude());
    }

    /**
     * Tests that segments are loaded correctly from an XML file.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("Load")
    public void testCorrectLoadSegment() throws Exception {
        optimodService.loadSegment("src/test/java/data/petitPlan.xml");

        Optional<Segment> segment = optimodService.findSegmentById(1L);
        assertTrue("The segment doesn't exist", segment.isPresent());
        assertEquals("The destination is incorrect", 25175778L, segment.get().getIdDestination());
        assertEquals("The longueur is incorrect", 69.979805, segment.get().getLength());
        assertEquals("The nomRue is incorrect", "Rue Danton", segment.get().getName());
        assertEquals("The origine is incorrect", 25175791L, segment.get().getIdOrigin());
    }

    /**
     * Tests that delivery requests are loaded correctly from an XML file.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("Load")
    public void testCorrectLoadDeliveryRequest() throws Exception {
        optimodService.loadDeliveryRequest("src/test/java/data/demandePetit1.xml");

        Optional<DeliveryRequest> deliveryRequest = optimodService.findDeliveryRequestById(2L);
        assertTrue("The delivery request doesn't exist", deliveryRequest.isPresent());
        assertEquals("The delivery request warehouse is incorrect", 342873658L, deliveryRequest.get().getIdWarehouse());
        assertEquals("The delivery request pickup address is incorrect", 208769039L, deliveryRequest.get().getIdPickup());
        assertEquals("The delivery request delivery address is incorrect", 25173820L, deliveryRequest.get().getIdDelivery());
    }

    /**
     * Tests that a non-XML file cannot be loaded as a node.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("NotXML")
    public void testNotXMLLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/notXML.xml");
        });
    }

    /**
     * Tests that a non-XML file cannot be loaded as a segment.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("NotXML")
    public void testNotXMLLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/notXML.xml");
        });
    }

    /**
     * Tests that a non-XML file cannot be loaded as a delivery request.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("NotXML")
    public void testNotXMLLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/notXML.xml");
        });
    }

    /**
     * Tests that a non-existing file cannot be loaded as a node.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("NonExisting")
    public void testNonExistingFileLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/nonExistingFile.xml");
        });
    }

    /**
     * Tests that a non-existing file cannot be loaded as a segment.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("NonExisting")
    public void testNonExistingFileLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/nonExistingFile.xml");
        });
    }

    /**
     * Tests that a non-existing file cannot be loaded as a delivery request.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("NonExisting")
    public void testNonExistingFileLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/nonExistingFile.xml");
        });
    }

    /**
     * Tests that the error handling of wrong tags is correct for nodes.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongReseauTagLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/wrongReseauTag.xml");
        });
        String expectedMessage = "No 'reseau' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong tags is correct for nodes.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongNoeudTagLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/wrongNoeudTag.xml");
        });
        String expectedMessage = "No 'noeud' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong tags is correct for segments.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongReseauTagLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/wrongReseauTag.xml");
        });
        String expectedMessage = "No 'reseau' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong tags is correct for segments.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongTronconTagLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/wrongTronconTag.xml");
        });
        String expectedMessage = "No 'troncon' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong tags is correct for delivery requests.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongEntrepotTagLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/wrongEntrepotTag.xml");
        });
        String expectedMessage = "No warehouse found in the first line of the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong tags is correct for delivery requests.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongLivraisonTagLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/wrongLivraisonTag.xml");
        });
        String expectedMessage = "No delivery request found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong tags is correct for delivery requests.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongTag")
    public void testWrongDemandeTagLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/wrongDemandeTag.xml");
        });
        String expectedMessage = "No 'demandeDeLivraisons' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for nodes.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongIdNodeAttributeLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/wrongIdNoeudAttribute.xml");
        });
        String expectedMessage = "No id found for the node : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for nodes.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongLatitudeNodeAttributeLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/wrongLatitudeNoeudAttribute.xml");
        });
        String expectedMessage = "No latitude found for the node : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for nodes.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongLongitudeNodeAttributeLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/data/wrongLongitudeNoeudAttribute.xml");
        });
        String expectedMessage = "No longitude found for the node : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for segments.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongDestinationSegmentAttributeLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/wrongDestinationTronconAttribute.xml");
        });
        String expectedMessage = "No destination found for the segment : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for segments.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongLengthSegmentAttributeLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/wrongLongueurTronconAttribute.xml");
        });
        String expectedMessage = "No length found for the segment : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for segments.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongOriginSegmentAttributeLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/data/wrongOrigineTronconAttribute.xml");
        });
        String expectedMessage = "No origin found for the segment : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for delivery requests.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongAdressWarehouseAttributeLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/wrongAdresseEntrepotAttribute.xml");
        });
        String expectedMessage = "No warehouse address found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for delivery requests.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongAdressPickupAttributeLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/wrongAdresseEnlevementAttribute.xml");
        });
        String expectedMessage = "No pickup address found for the delivery request : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the error handling of wrong attributes is correct for delivery requests.
     * @throws Exception if an error occurs during loading
     */
    @Test
    @Tag("WrongAttribute")
    public void testWrongAdressDeliveryAttributeLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/data/wrongAdresseLivraisonAttribute.xml");
        });
        String expectedMessage = "No delivery address found for the delivery request : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the TSP functions are working correctly by validating the graph.
     * @throws Exception if an error occurs during validation
     */
    @Test
    @Tag("TSP")
    public void testValidateGraph() throws Exception {
        optimodService.loadSegment("src/test/java/data/petitPlanTest.xml");
        optimodService.loadDeliveryRequest("src/test/java/data/demandePetit1.xml");

        Exception exception = assertThrows(Exception.class, () -> {
            List<Long> route = optimodService.calculateOptimalRoute();
        });
        String expectedMessage = "Graph does not contain nodes for delivery request: ";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    /**
     * Tests that the TSP functions are working correctly by calculating the optimal route.
     * @throws Exception if an error occurs during calculation
     */
    @Test
    @Tag("TSP")
    public void testCalculateOptimalRoute() throws Exception {
        optimodService.loadNode("src/test/java/data/petitPlanTest.xml");
        optimodService.loadSegment("src/test/java/data/petitPlanTest.xml");
        optimodService.loadDeliveryRequest("src/test/java/data/demandePetit1Test.xml");

        List<Long> route = optimodService.calculateOptimalRoute();

        assertTrue("The route is incorrect", route.get(0) == 25175791L);
        assertTrue("The route is incorrect", route.get(1) == 2129259178L);
        assertTrue("The route is incorrect", route.get(2) == 26086130L);
    }
}