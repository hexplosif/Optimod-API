package com.hexplosif.OptimodBackEnd.service;

import ch.qos.logback.core.joran.sanity.Pair;
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
import java.util.*;
import java.util.stream.Collectors;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("SpellCheckingInspection")
@Data
@Service
public class OptimodService {

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
     */
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
     */
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
     */
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
     */

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
     */
    public Optional<Node> findNodeById(Long id) {
        return nodeRepository.findById(id);
    }

    /**
     * Get all nodes
     * @return The list of nodes
     */
    public Iterable<Node> findAllNodes() {
        return nodeRepository.findAll();
    }

    /**
     * Delete a node by its id
     * @param id The id of the node
     */
    public void deleteNodeById(Long id) {
        nodeRepository.deleteById(id);
    }

    /**
     * Save a node
     * @param node The node to save
     * @return The saved node
     */
    public Node saveNode(Node node) {
        Node savedNode;
        savedNode = nodeRepository.save(node);
        return savedNode;
    }

    /**
     * Create a node
     * @param node The node to create
     */
    public void createNode(Node node) {
        nodeRepository.save(node);
    }

    /**
     * Delete all nodes
     */
    public void deleteAllNodes() {
        nodeRepository.deleteAll();
    }

    /**
     * Create nodes
     *
     * @param nodes The list of nodes to create
     * @return The list of nodes created
     */
    public Iterable<Node> createNodes(Iterable<Node> nodes) {
        nodeRepository.saveAll(nodes);
        return nodes;
    }

    /**
     * Get a segment by its id
     *
     * @param id The id of the segment
     * @return The segment
     */
    public Optional<Segment> findSegmentById(Long id) {
        return segmentRepository.findById(id);
    }

    /**
     * Get all segments
     * @return The list of segments
     */
    public Iterable<Segment> findAllSegments() {
        return segmentRepository.findAll();
    }

    /**
     * Delete a segment by its id
     * @param id The id of the segment
     */
    public void deleteSegmentById(Long id) {
        segmentRepository.deleteById(id);
    }

    /**
     * Save a segment
     * @param segment The segment to save
     * @return The saved segment
     */
    public Segment saveSegment(Segment segment) {
        Segment savedSegment;
        savedSegment = segmentRepository.save(segment);
        return savedSegment;
    }

    /**
     * Create a segment
     * @param segment The segment to create
     */
    public void createSegment(Segment segment) {
        segmentRepository.save(segment);
    }

    /**
     * Delete all segments
     */
    public void deleteAllSegments() {
        segmentRepository.deleteAll();
    }

    /**
     * Create segments
     *
     * @param segments The list of segments to create
     * @return The list of segments created
     */
    public Iterable<Segment> createSegments(Iterable<Segment> segments) {
        segmentRepository.saveAll(segments);
        return segments;
    }

    /**
     * Get a delivery request by its id
     *
     * @param id The id of the delivery request
     * @return The delivery request
     */
    public Optional<DeliveryRequest> findDeliveryRequestById(Long id) {
        return deliveryRequestRepository.findById(id);
    }

    /**
     * Get all delivery requests
     * @return The list of delivery requests
     */
    public Iterable<DeliveryRequest> findAllDeliveryRequests() {
        return deliveryRequestRepository.findAll();
    }

    /**
     * Delete a delivery request by its id
     * @param id The id of the delivery request
     */
    public void deleteDeliveryRequestById(Long id) {
        deliveryRequestRepository.deleteById(id);
    }

    /**
     * Save a delivery request
     * @param delivery_request The delivery request to save
     * @return The saved delivery request
     */
    public DeliveryRequest saveDeliveryRequest(DeliveryRequest delivery_request) {
        DeliveryRequest savedDeliveryRequest;
        savedDeliveryRequest = deliveryRequestRepository.save(delivery_request);
        return savedDeliveryRequest;
    }

    /**
     * Create a delivery request
     * @param delivery_request The delivery request to create
     */
    public void createDeliveryRequest(DeliveryRequest delivery_request) {
        deliveryRequestRepository.save(delivery_request);
    }

    /**
     * Delete all delivery requests
     */
    public void deleteAllDeliveryRequests() {
        deliveryRequestRepository.deleteAll();
    }

    /**
     * Get a courier by its id
     *
     * @param id The id of the courier
     * @return The courier
     */
    public Optional<Courier> findCourierById(Long id) {
        return courierRepository.findById(id);
    }

    /**
     * Get all couriers
     * @return The list of couriers
     */
    public Iterable<Courier> findAllCouriers() {
        return courierRepository.findAll();
    }

    /**
     * Delete a courier by its id
     * @param id The id of the courier
     */
    public void deleteCourierById(Long id) {
        // Delete it if it is not assigned to any delivery request
        Iterable<DeliveryRequest> deliveryRequests = deliveryRequestRepository.findAll();
        boolean isAssigned = false;
        for (DeliveryRequest deliveryRequest : deliveryRequests) {
            if (deliveryRequest.getIdCourier().equals(id)) {
                isAssigned = true;
                break;
            }
        }
        if (!isAssigned) {
            courierRepository.deleteById(id);
        } else {
            throw new IllegalStateException("The courier is assigned to a delivery request.");
        }
    }

    /**
     * Save a courier
     * @param delivery_request The courier to save
     * @return The saved courier
     */
    public Courier saveCourier(Courier delivery_request) {
        Courier savedCourier;
        savedCourier = courierRepository.save(delivery_request);
        return savedCourier;
    }

    /**
     * Create a courier
     * @param delivery_request The courier to create
     */
    public void createCourier(Courier delivery_request) {
        courierRepository.save(delivery_request);
    }

    /**
     * Delete all couriers
     */
    public void deleteAllCouriers() {
        courierRepository.deleteAll();
    }

    /**
     * Add a courier
     */
    public void addCourier() {
        Courier courier = new Courier();
        courier.setName("Courier " + (courierRepository.count() + 1));
        courierRepository.save(courier);
    }

    /**
     * Delete the last courier
     */
    public void deleteCourier() {
        List<Courier> couriers = (List<Courier>) courierRepository.findAll();
        if (!couriers.isEmpty()) {
            // Delete it if it is not assigned to any delivery request
            Iterable<DeliveryRequest> deliveryRequests = deliveryRequestRepository.findAll();
            boolean isAssigned = false;
            for (DeliveryRequest deliveryRequest : deliveryRequests) {
                if (deliveryRequest.getIdCourier().equals(couriers.get(couriers.size() - 1).getId())) {
                    isAssigned = true;
                    break;
                }
            }
            if (!isAssigned) {
                courierRepository.deleteById(couriers.get(couriers.size() - 1).getId());
            } else {
                throw new IllegalStateException("The courier is assigned to a delivery request.");
            }
        }
    }

    /**
     * Assign a courier to a delivery request
     * @param idCourier The id of the courier
     * @param idDeliveryRequest The id of the delivery request
     * @return The delivery request
     */
    public DeliveryRequest assignCourier(Long idCourier, Long idDeliveryRequest) {
        Optional<Courier> courier = courierRepository.findById(idCourier);
        Optional<DeliveryRequest> deliveryRequest = deliveryRequestRepository.findById(idDeliveryRequest);

        if (courier.isPresent() && deliveryRequest.isPresent()) {
            deliveryRequest.get().setIdCourier(idCourier);
            deliveryRequestRepository.save(deliveryRequest.get());
        }

        return deliveryRequest.get();
    }

    /**
     * Calculate the optimal route.
     * @return The list of node IDs representing the optimal route.
     */
    public List<List<Long>> calculateOptimalRoute() {
        // Fetch all delivery requests
        List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();

        if (deliveryRequests.isEmpty()) {
            throw new IllegalStateException("No delivery requests found.");
        }

        // Fetch all couriers
        List<Courier> courierList = (List<Courier>) courierRepository.findAll();

        List<List<Long>> listeRoutes = new ArrayList<>();

        int nbCouriers = courierList.size();

        for (int i = 0; i < nbCouriers; i++) {
            int finalI = i;
            List<DeliveryRequest> deliveryRequestsCourier = deliveryRequests.stream()
                    .filter(deliveryRequest -> deliveryRequest.getIdCourier().equals(courierList.get(finalI).getId()))
                    .collect(Collectors.toList());

            if (deliveryRequestsCourier.isEmpty()) {
                listeRoutes.add(new ArrayList<>());
            }
            else {
                // Build the graph from segments
                Map<Long, Map<Long, Double>> graph = buildGraph();
                // Validate the graph contains all necessary nodes
                validateGraph(graph, deliveryRequestsCourier);
                // Calculate the optimal route
                List<Long> route = findOptimalRoute(graph, deliveryRequestsCourier);
                listeRoutes.add(route);
            }
        }

        System.out.println("Liste des routes : " + listeRoutes);

        return listeRoutes;
    }

    private Map<Long, Map<Long, Double>> buildGraph() {
        Map<Long, Map<Long, Double>> graph = new HashMap<>();

        List<Segment> segments = (List<Segment>) segmentRepository.findAll();
        for (Segment segment : segments) {
            graph.putIfAbsent(segment.getIdOrigin(), new HashMap<>());
            graph.get(segment.getIdOrigin()).put(segment.getIdDestination(), segment.getLength());

            graph.putIfAbsent(segment.getIdDestination(), new HashMap<>());
            graph.get(segment.getIdDestination()).put(segment.getIdOrigin(), segment.getLength());
        }

        return graph;
    }

    private void validateGraph(Map<Long, Map<Long, Double>> graph, List<DeliveryRequest> deliveryRequests) {
        for (DeliveryRequest request : deliveryRequests) {
            if (!graph.containsKey(request.getIdPickup()) || !graph.containsKey(request.getIdDelivery())) {
                throw new IllegalStateException("Graph does not contain nodes for delivery request: " + request);
            }
        }
    }

    private List<Long> findOptimalRoute(Map<Long, Map<Long, Double>> graph, List<DeliveryRequest> deliveryRequests) {
        List<Long> route = new ArrayList<>();
        Long warehouseId = deliveryRequests.get(0).getIdWarehouse();
        route.add(warehouseId); // Start at the warehouse

        Set<Long> visitedPickups = new HashSet<>();
        Set<Long> visitedDeliveries = new HashSet<>();

        PriorityQueue<Long> toVisit = new PriorityQueue<>(Comparator.comparingDouble(node -> shortestDistance(graph, route.get(route.size() - 1), node)));
        Map<Long, DeliveryRequest> deliveryMap = deliveryRequests.stream()
                .collect(Collectors.toMap(DeliveryRequest::getIdPickup, request -> request));

        // Add initial pickups to visit
        toVisit.addAll(deliveryRequests.stream().map(DeliveryRequest::getIdPickup).toList());

        while (!toVisit.isEmpty()) {
            Long current = toVisit.poll();

            // Calculate the shortest path from the current node to the next target
            List<Long> path = dijkstraPath(graph, route.get(route.size() - 1), current);

            // Append the intermediate nodes to the route
            path.remove(0); // Avoid duplicating the current node
            route.addAll(path);

            if (visitedPickups.contains(current)) {
                // Visit the delivery corresponding to the current pickup
                DeliveryRequest request = deliveryMap.get(current);
                if (request == null) {
                    throw new IllegalStateException("DeliveryRequest not found for pickup ID: " + current);
                }

                Long deliveryId = request.getIdDelivery();
                if (!visitedDeliveries.contains(deliveryId)) {
                    toVisit.add(deliveryId);
                    visitedDeliveries.add(deliveryId);
                }
            } else {
                // Visit the pickup
                visitedPickups.add(current);

                // Add the corresponding delivery to the queue
                DeliveryRequest request = deliveryMap.get(current);
                if (request != null) {
                    Long deliveryId = request.getIdDelivery();
                    if (!visitedDeliveries.contains(deliveryId)) {
                        toVisit.add(deliveryId);
                    }
                }

                // Add other pickups to the queue if not already visited
                for (DeliveryRequest otherRequest : deliveryRequests) {
                    if (!visitedPickups.contains(otherRequest.getIdPickup()) && !toVisit.contains(otherRequest.getIdPickup())) {
                        toVisit.add(otherRequest.getIdPickup());
                    }
                }
            }
        }

        // Return to the warehouse
        List<Long> returnPath = dijkstraPath(graph, route.get(route.size() - 1), warehouseId);
        returnPath.remove(0); // Avoid duplicating the current node
        route.addAll(returnPath);

        return route;
    }

    private List<Long> dijkstraPath(Map<Long, Map<Long, Double>> graph, Long start, Long target) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previousNodes = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Long node : graph.keySet()) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Long current = pq.poll();

            if (current.equals(target)) break;

            Map<Long, Double> neighbors = graph.getOrDefault(current, Collections.emptyMap());
            for (Map.Entry<Long, Double> entry : neighbors.entrySet()) {
                Long neighbor = entry.getKey();
                Double newDist = distances.get(current) + entry.getValue();

                if (newDist < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        // Backtrack to construct the path
        List<Long> path = new ArrayList<>();
        for (Long at = target; at != null; at = previousNodes.get(at)) {
            path.add(0, at);
        }

        return path;
    }

    private double shortestDistance(Map<Long, Map<Long, Double>> graph, Long start, Long end) {
        if (!graph.containsKey(start) || !graph.containsKey(end)) return Double.MAX_VALUE;

        Map<Long, Double> distances = new HashMap<>();
        PriorityQueue<Long> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        distances.put(start, 0.0);

        pq.add(start);

        while (!pq.isEmpty()) {
            Long current = pq.poll();

            if (current.equals(end)) break;

            Map<Long, Double> neighbors = graph.getOrDefault(current, Collections.emptyMap());
            for (Map.Entry<Long, Double> entry : neighbors.entrySet()) {
                Long neighbor = entry.getKey();
                Double newDist = distances.getOrDefault(current, Double.MAX_VALUE) + entry.getValue();

                if (newDist < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                    pq.add(neighbor);
                }
            }
        }

        return distances.getOrDefault(end, Double.MAX_VALUE);
    }






}
