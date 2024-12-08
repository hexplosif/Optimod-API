package com.hexplosif.OptimodBackEnd.service;

import com.hexplosif.OptimodBackEnd.model.*;
import com.hexplosif.OptimodBackEnd.repository.CourierRepository;
import com.hexplosif.OptimodBackEnd.repository.DeliveryRequestRepository;
import com.hexplosif.OptimodBackEnd.repository.NodeRepository;
import com.hexplosif.OptimodBackEnd.repository.SegmentRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class poubelle {
/*
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private SegmentRepository segmentRepository;

    @Autowired
    private DeliveryRequestRepository deliveryRequestRepository;

    @Autowired
    private CourierRepository courierRepository;

    /**
     * Parse the XML file
     * @param file The XML file
     * @return The document

    private Document parseXMLFile(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Load the nodes from the XML file
     * @param XMLFileName The XML file

    public void loadNode(String XMLFileName) throws Exception{

        try {
            File XMLFile = new File(XMLFileName);
            Document document = parseXMLFile(XMLFile);

            // Check if <reseau> is present
            if (!document.getDocumentElement().getNodeName().equals("reseau")) {
                throw new Exception("No 'reseau' tag found in the XML file");
            }

            NodeList nodeList = document.getElementsByTagName("noeud");

            // Integrity check
            if (nodeList.getLength() == 0) {
                throw new Exception("No 'noeud' tag found in the XML file");
            }

            List<Node> tmpListNodes = (List<Node>) nodeRepository.findAll();

            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node noeud = nodeList.item(i);

                if (noeud.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementNoeud = (Element) noeud;

                    String idNoeud = elementNoeud.getAttribute("id");

                    // Integrity check
                    if (idNoeud.isEmpty()) {
                        throw new Exception("No id found for the node : " + i);
                    }
                    String latitudeNoeud = elementNoeud.getAttribute("latitude");

                    // Integrity check
                    if (latitudeNoeud.isEmpty()) {
                        throw new Exception("No latitude found for the node : " + i);
                    }

                    String longitudeNoeud = elementNoeud.getAttribute("longitude");

                    // Integrity check
                    if (longitudeNoeud.isEmpty()) {
                        throw new Exception("No longitude found for the node : " + i);
                    }

                    Node node = new Node();
                    node.setId(Long.parseLong(idNoeud));
                    node.setLatitude(Double.parseDouble(latitudeNoeud));
                    node.setLongitude(Double.parseDouble(longitudeNoeud));

                    //System.out.println("Node: " + node.getId() + ", Latitude: " + node.getLatitude() + ", Longitude: " + node.getLongitude());
                    tmpListNodes.add(node);
                }
            }

            nodeRepository.saveAll(tmpListNodes);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Load the segments from the XML file
     * @param XMLFileName The XML file

    public void loadSegment(String XMLFileName) throws Exception {

        try {
            File XMLFile = new File(XMLFileName);
            Document document = parseXMLFile(XMLFile);

            // Check if <reseau> is present
            if (!document.getDocumentElement().getNodeName().equals("reseau")) {
                throw new Exception("No 'reseau' tag found in the XML file");
            }

            NodeList listeTroncons = document.getElementsByTagName("troncon");

            // Integrity check
            if (listeTroncons.getLength() == 0) {
                throw new Exception("No 'troncon' tag found in the XML file");
            }

            List<Segment> tmpListSegments = (List<Segment>) segmentRepository.findAll();

            for (int i = 0; i < listeTroncons.getLength(); i++) {
                org.w3c.dom.Node troncon = listeTroncons.item(i);

                if (troncon.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementTroncon = (Element) troncon;

                    String origineTroncon = elementTroncon.getAttribute("origine");
                    // Integrity check
                    if (origineTroncon.isEmpty()) {
                        throw new Exception("No origin found for the segment : " + i);
                    }

                    String destinationTroncon = elementTroncon.getAttribute("destination");
                    // Integrity check
                    if (destinationTroncon.isEmpty()) {
                        throw new Exception("No destination found for the segment : " + i);
                    }

                    String longueurTroncon = elementTroncon.getAttribute("longueur");
                    // Integrity check
                    if (longueurTroncon.isEmpty()) {
                        throw new Exception("No length found for the segment : " + i);
                    }

                    String nomRueTroncon = elementTroncon.getAttribute("nomRue");
                    // Il n'y a pas de nom de rue pour tous les segments

                    Segment segment = new Segment();
                    segment.setIdOrigin(Long.parseLong(origineTroncon));
                    segment.setIdDestination(Long.parseLong(destinationTroncon));
                    segment.setLength(Double.parseDouble(longueurTroncon));
                    segment.setName(nomRueTroncon);

                    //System.out.println("Segment: " + segment.getIdOrigin() + ", Destination: " + segment.getIdDestination() + ", Length: " + segment.getLength() + ", Name: " + segment.getName());
                    tmpListSegments.add(segment);
                }
            }

            segmentRepository.saveAll(tmpListSegments);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Load the delivery request from the XML file
     * @param XMLDeliveryRequest The XML delivery request file


    public void loadDeliveryRequest(String XMLDeliveryRequest) throws Exception {

        try {
            File XMLFile = new File(XMLDeliveryRequest);
            Document document = parseXMLFile(XMLFile);

            // Check if <demandeDeLivraisons> is present
            if (!document.getDocumentElement().getNodeName().equals("demandeDeLivraisons")) {
                throw new Exception("No 'demandeDeLivraisons' tag found in the XML file");
            }

            NodeList listeLivraisons = document.getElementsByTagName("livraison");
            // Integrity check
            if (listeLivraisons.getLength() == 0) {
                throw new Exception("No delivery request found in the XML file");
            }

            org.w3c.dom.Node warehouse = document.getElementsByTagName("entrepot").item(0); // Warehouse is the first element
            // Integrity check
            if (warehouse == null) {
                throw new Exception("No warehouse found in the first line of the XML file");
            }

            String warehouseAddress = ((Element) warehouse).getAttribute("adresse");
            // Integrity check
            if (warehouseAddress.isEmpty()) {
                throw new Exception("No warehouse address found in the XML file");
            }

            for (int i = 0; i < listeLivraisons.getLength(); i++) {
                org.w3c.dom.Node livraison = listeLivraisons.item(i);

                if (livraison.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementLivraison = (Element) livraison;

                    String adresseEnlevement = elementLivraison.getAttribute("adresseEnlevement");
                    // Integrity check
                    if (adresseEnlevement.isEmpty()) {
                        throw new Exception("No pickup address found for the delivery request : " + i);
                    }

                    String adresseLivraison = elementLivraison.getAttribute("adresseLivraison");
                    // Integrity check
                    if (adresseLivraison.isEmpty()) {
                        throw new Exception("No delivery address found for the delivery request : " + i);
                    }

                    DeliveryRequest deliveryRequest = new DeliveryRequest();
                    deliveryRequest.setIdDelivery(Long.parseLong(adresseLivraison));
                    deliveryRequest.setIdPickup(Long.parseLong(adresseEnlevement));
                    deliveryRequest.setIdWarehouse(Long.parseLong(warehouseAddress));

                    //System.out.println("DeliveryRequest: " + deliveryRequest.getIdDelivery() + ", Pickup: " + deliveryRequest.getIdPickup() + ", WarehouseLocation: " + deliveryRequest.getIdWarehouse());
                    deliveryRequestRepository.save(deliveryRequest);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Get a node by its id
     *
     * @param id The id of the node
     * @return The node

    public Optional<Node> findNodeById(Long id) {
        return nodeRepository.findById(id);
    }

    /**
     * Get all nodes
     * @return The list of nodes

    public Iterable<Node> findAllNodes() {
        return nodeRepository.findAll();
    }

    /**
     * Delete a node by its id
     * @param id The id of the node

    public void deleteNodeById(Long id) {
        nodeRepository.deleteById(id);
    }

    /**
     * Save a node
     * @param node The node to save
     * @return The saved node

    public Node saveNode(Node node) {
        Node savedNode;
        savedNode = nodeRepository.save(node);
        return savedNode;
    }

    /**
     * Create a node
     * @param node The node to create

    public void createNode(Node node) {
        nodeRepository.save(node);
    }

    /**
     * Delete all nodes

    public void deleteAllNodes() {
        nodeRepository.deleteAll();
    }

    /**
     * Create nodes
     *
     * @param nodes The list of nodes to create
     * @return The list of nodes created

    public Iterable<Node> createNodes(Iterable<Node> nodes) {
        nodeRepository.saveAll(nodes);
        return nodes;
    }

    /**
     * Get a segment by its id
     *
     * @param id The id of the segment
     * @return The segment

    public Optional<Segment> findSegmentById(Long id) {
        return segmentRepository.findById(id);
    }

    /**
     * Get all segments
     * @return The list of segments

    public Iterable<Segment> findAllSegments() {
        return segmentRepository.findAll();
    }

    /**
     * Delete a segment by its id
     * @param id The id of the segment

    public void deleteSegmentById(Long id) {
        segmentRepository.deleteById(id);
    }

    /**
     * Save a segment
     * @param segment The segment to save
     * @return The saved segment

    public Segment saveSegment(Segment segment) {
        Segment savedSegment;
        savedSegment = segmentRepository.save(segment);
        return savedSegment;
    }

    /**
     * Create a segment
     * @param segment The segment to create

    public void createSegment(Segment segment) {
        segmentRepository.save(segment);
    }

    /**
     * Delete all segments

    public void deleteAllSegments() {
        segmentRepository.deleteAll();
    }

    /**
     * Create segments
     *
     * @param segments The list of segments to create
     * @return The list of segments created

    public Iterable<Segment> createSegments(Iterable<Segment> segments) {
        segmentRepository.saveAll(segments);
        return segments;
    }

    /**
     * Get a delivery request by its id
     *
     * @param id The id of the delivery request
     * @return The delivery request

    public Optional<DeliveryRequest> findDeliveryRequestById(Long id) {
        return deliveryRequestRepository.findById(id);
    }

    /**
     * Get all delivery requests
     * @return The list of delivery requests

    public Iterable<DeliveryRequest> findAllDeliveryRequests() {
        return deliveryRequestRepository.findAll();
    }

    /**
     * Delete a delivery request by its id
     * @param id The id of the delivery request

    public void deleteDeliveryRequestById(Long id) {
        deliveryRequestRepository.deleteById(id);
    }

    /**
     * Save a delivery request
     * @param delivery_request The delivery request to save
     * @return The saved delivery request

    public DeliveryRequest saveDeliveryRequest(DeliveryRequest delivery_request) {
        DeliveryRequest savedDeliveryRequest;
        savedDeliveryRequest = deliveryRequestRepository.save(delivery_request);
        return savedDeliveryRequest;
    }

    /**
     * Create a delivery request
     * @param delivery_request The delivery request to create

    public void createDeliveryRequest(DeliveryRequest delivery_request) {
        deliveryRequestRepository.save(delivery_request);
    }

    /**
     * Delete all delivery requests

    public void deleteAllDeliveryRequests() {
        deliveryRequestRepository.deleteAll();
    }

    /**
     * Get a courier by its id
     *
     * @param id The id of the courier
     * @return The courier

    public Optional<Courier> findCourierById(Long id) {
        return courierRepository.findById(id);
    }

    /**
     * Get all couriers
     * @return The list of couriers

    public Iterable<Courier> findAllCouriers() {
        return courierRepository.findAll();
    }

    /**
     * Delete a courier by its id
     * @param id The id of the courier

    public void deleteCourierById(Long id) {
        courierRepository.deleteById(id);
    }

    /**
     * Save a courier
     * @param delivery_request The courier to save
     * @return The saved courier

    public Courier saveCourier(Courier delivery_request) {
        Courier savedCourier;
        savedCourier = courierRepository.save(delivery_request);
        return savedCourier;
    }

    /**
     * Create a courier
     * @param delivery_request The courier to create

    public void createCourier(Courier delivery_request) {
        courierRepository.save(delivery_request);
    }

    /**
     * Delete all couriers

    public void deleteAllCouriers() {
        courierRepository.deleteAll();
    }

    /**
     * Assign a courier to a delivery request
     * @param idCourier The id of the courier
     * @param idDeliveryRequest The id of the delivery request
     * @return The delivery request

    public DeliveryRequest assignCourier(Long idCourier, Long idDeliveryRequest) {
        Optional<Courier> courier = courierRepository.findById(idCourier);
        Optional<DeliveryRequest> deliveryRequest = deliveryRequestRepository.findById(idDeliveryRequest);

        if (courier.isPresent() && deliveryRequest.isPresent()) {
            deliveryRequest.get().setIdCourier(idCourier);
            deliveryRequestRepository.save(deliveryRequest.get());
        }

        return deliveryRequest.get();
    }

    @Autowired
    private DijkstraService dijkstraService;

    /**
     * Calculate the optimal route


/*
    public void calculateOptimalRoute() {
        Long warehouseId = 25610888L; // Example warehouse ID
        List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();

        List<Long> optimalRoute = dijkstraService.findOptimalRoute(warehouseId, deliveryRequests);
        System.out.println("Optimal Route: " + optimalRoute);
    }

    public List<Long> calculateOptimalRoute() {
        Long warehouseId = 2835339774L; // Replace with actual logic to fetch warehouse ID
        List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();

        return dijkstraService.findOptimalRoute(warehouseId, deliveryRequests);
    }
*/
}
