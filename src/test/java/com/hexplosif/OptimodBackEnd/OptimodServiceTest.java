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

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // Those tests make sure that the nodes, segments and delivery requests are loaded correctly
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
        assertTrue("The delivery request doesn't exist", deliveryRequest.isPresent());
        assertEquals("The delivery request warehouse is incorrect",342873658L, deliveryRequest.get().getIdWarehouse());
        assertEquals("The delivery request pickup address is incorrect",208769039L, deliveryRequest.get().getIdPickup());
        assertEquals("The delivery request delivery address is incorrect",25173820L, deliveryRequest.get().getIdDelivery());
    }

    // Those tests make sure that a non XML file can't be loaded
    @Test
    public void testNotXMLLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/notXML.xml");
        });
    }

    @Test
    public void testNotXMLLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/notXML.xml");
        });
    }

    @Test
    public void testNotXMLLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/notXML.xml");
        });
    }

    // Those tests make sure that a non existing file can't be loaded
    @Test
    public void testNonExistingFileLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/nonExistingFile.xml");
        });
    }

    @Test
    public void testNonExistingFileLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/nonExistingFile.xml");
        });
    }

    @Test
    public void testNonExistingFileLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/nonExistingFile.xml");
        });
    }

    // Those tests make sure that the error handling of wrong tags is correct
    @Test
    public void testWrongReseauTagLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/wrongReseauTag.xml");
        });
        String expectedMessage = "No 'reseau' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongNoeudTagLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/wrongNoeudTag.xml");
        });
        String expectedMessage = "No 'noeud' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongReseauTagLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/wrongReseauTag.xml");
        });
        String expectedMessage = "No 'reseau' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongTronconTagLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/wrongTronconTag.xml");
        });
        String expectedMessage = "No 'troncon' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongEntrepotTagLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/wrongEntrepotTag.xml");
        });
        String expectedMessage = "No warehouse found in the first line of the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongLivraisonTagLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/wrongLivraisonTag.xml");
        });
        String expectedMessage = "No delivery request found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongDemandeTagLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/wrongDemandeTag.xml");
        });
        String expectedMessage = "No 'demandeDeLivraisons' tag found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    // Those tests make sure that the error handling of wrong attributes is correct
    @Test
    public void testWrongIdNodeAttributeLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/wrongIdNoeudAttribute.xml");
        });
        String expectedMessage = "No id found for the node : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongLatitudeNodeAttributeLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/wrongLatitudeNoeudAttribute.xml");
        });
        String expectedMessage = "No latitude found for the node : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongLongitudeNodeAttributeLoadNode() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadNode("src/test/java/testResources/wrongLongitudeNoeudAttribute.xml");
        });
        String expectedMessage = "No longitude found for the node : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongDestinationSegmentAttributeLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/wrongDestinationTronconAttribute.xml");
        });
        String expectedMessage = "No destination found for the segment : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongLengthSegmentAttributeLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/wrongLongueurTronconAttribute.xml");
        });
        String expectedMessage = "No length found for the segment : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongOriginSegmentAttributeLoadSegment() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadSegment("src/test/java/testResources/wrongOrigineTronconAttribute.xml");
        });
        String expectedMessage = "No origin found for the segment : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongAdressWarehouseAttributeLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/wrongAdresseEntrepotAttribute.xml");
        });
        String expectedMessage = "No warehouse address found in the XML file";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongAdressPickupAttributeLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/wrongAdresseEnlevementAttribute.xml");
        });
        String expectedMessage = "No pickup address found for the delivery request : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }

    @Test
    public void testWrongAdressDeliveryAttributeLoadDeliveryRequest() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> {
            optimodService.loadDeliveryRequest("src/test/java/testResources/wrongAdresseLivraisonAttribute.xml");
        });
        String expectedMessage = "No delivery address found for the delivery request : ";
        String actualMessage = exception.getMessage();
        assertTrue("The exception message is incorrect", actualMessage.contains(expectedMessage));
    }
}

