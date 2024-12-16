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
     * Load the nodes from the XML file
     *
     * @param XMLFileName The XML file
     */
    public void loadNode(String XMLFileName) throws Exception {

        try {
            File XMLFile = new File(XMLFileName);
            Document document = parseXMLFile(XMLFile);

            // Check if <reseau> is present
            if (!document.getDocumentElement().getNodeName().equals("reseau")) {
                throw new IllegalStateException("No 'reseau' tag found in the XML file");
            }

            NodeList nodeList = document.getElementsByTagName("noeud");

            // Integrity check
            if (nodeList.getLength() == 0) {
                throw new IllegalStateException("No 'noeud' tag found in the XML file");
            }

            List<Node> tmpListNodes = (List<Node>) nodeRepository.findAll();

            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node noeud = nodeList.item(i);

                if (noeud.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Node node = getNode((Element) noeud, i);

                    tmpListNodes.add(node);
                }
            }

            nodeRepository.saveAll(tmpListNodes);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private static Node getNode(Element noeud, int i) {
        Element elementNoeud = noeud;

        String idNoeud = elementNoeud.getAttribute("id");

        // Integrity check
        if (idNoeud.isEmpty()) {
            throw new IllegalStateException("No id found for the node : " + i);
        }
        String latitudeNoeud = elementNoeud.getAttribute("latitude");

        // Integrity check
        if (latitudeNoeud.isEmpty()) {
            throw new IllegalStateException("No latitude found for the node : " + i);
        }

        String longitudeNoeud = elementNoeud.getAttribute("longitude");

        // Integrity check
        if (longitudeNoeud.isEmpty()) {
            throw new IllegalStateException("No longitude found for the node : " + i);
        }

        Node node = new Node();
        node.setId(Long.parseLong(idNoeud));
        node.setLatitude(Double.parseDouble(latitudeNoeud));
        node.setLongitude(Double.parseDouble(longitudeNoeud));
        return node;
    }

    /**
     * Load the segments from the XML file
     * @param XMLFileName The XML file
     * @throws Exception If an error occurs
     */
    public void loadSegment(String XMLFileName) throws Exception {

        try {
            File XMLFile = new File(XMLFileName);
            Document document = parseXMLFile(XMLFile);

            // Check if <reseau> is present
            if (!document.getDocumentElement().getNodeName().equals("reseau")) {
                throw new IllegalStateException("No 'reseau' tag found in the XML file");
            }

            NodeList listeTroncons = document.getElementsByTagName("troncon");

            // Integrity check
            if (listeTroncons.getLength() == 0) {
                throw new IllegalStateException("No 'troncon' tag found in the XML file");
            }

            List<Segment> tmpListSegments = (List<Segment>) segmentRepository.findAll();

            for (int i = 0; i < listeTroncons.getLength(); i++) {
                org.w3c.dom.Node troncon = listeTroncons.item(i);

                if (troncon.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementTroncon = (Element) troncon;

                    String origineTroncon = elementTroncon.getAttribute("origine");
                    // Integrity check
                    if (origineTroncon.isEmpty()) {
                        throw new IllegalStateException("No origin found for the segment : " + i);
                    }

                    String destinationTroncon = elementTroncon.getAttribute("destination");
                    // Integrity check
                    if (destinationTroncon.isEmpty()) {
                        throw new IllegalStateException("No destination found for the segment : " + i);
                    }

                    String longueurTroncon = elementTroncon.getAttribute("longueur");
                    // Integrity check
                    if (longueurTroncon.isEmpty()) {
                        throw new IllegalStateException("No length found for the segment : " + i);
                    }

                    String nomRueTroncon = elementTroncon.getAttribute("nomRue");
                    // Il n'y a pas de nom de rue pour tous les segments

                    Segment segment = new Segment();
                    segment.setIdOrigin(Long.parseLong(origineTroncon));
                    segment.setIdDestination(Long.parseLong(destinationTroncon));
                    segment.setLength(Double.parseDouble(longueurTroncon));
                    segment.setName(nomRueTroncon);

                    tmpListSegments.add(segment);
                }
            }

            segmentRepository.saveAll(tmpListSegments);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Load the delivery request from the XML file
     *
     * @param XMLDeliveryRequest The XML delivery request file
     */

    public void loadDeliveryRequest(String XMLDeliveryRequest) throws Exception {

        try {
            File XMLFile = new File(XMLDeliveryRequest);
            Document document = parseXMLFile(XMLFile);

            // Check if <demandeDeLivraisons> is present
            if (!document.getDocumentElement().getNodeName().equals("demandeDeLivraisons")) {
                throw new IllegalStateException("No 'demandeDeLivraisons' tag found in the XML file");
            }

            NodeList listeLivraisons = document.getElementsByTagName("livraison");
            // Integrity check
            if (listeLivraisons.getLength() == 0) {
                throw new IllegalStateException("No delivery request found in the XML file");
            }

            org.w3c.dom.Node warehouse = document.getElementsByTagName("entrepot").item(0); // Warehouse is the first element
            // Integrity check
            if (warehouse == null) {
                throw new IllegalStateException("No warehouse found in the first line of the XML file");
            }

            String warehouseAddress = ((Element) warehouse).getAttribute("adresse");
            // Integrity check
            if (warehouseAddress.isEmpty()) {
                throw new IllegalStateException("No warehouse address found in the XML file");
            }
            // Check if the warehouse is already in the database, if not throw an exception
            Optional<Node> node = nodeRepository.findById(Long.parseLong(warehouseAddress));
            if (node.isEmpty()) {
                throw new IllegalStateException("The warehouse address is not in the map, please load a bigger map including the warehouse address");
            }

            // Check if the warehouse is the same as other delivery requests that are already in the database, if not throw an exception
            List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();
            for (DeliveryRequest deliveryRequest : deliveryRequests) {
                if (deliveryRequest.getIdWarehouse() != Long.parseLong(warehouseAddress)) {
                    throw new IllegalStateException("The warehouse address is different from the one already in the database");
                }
            }

            for (int i = 0; i < listeLivraisons.getLength(); i++) {
                org.w3c.dom.Node livraison = listeLivraisons.item(i);

                if (livraison.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element elementLivraison = (Element) livraison;

                    String adresseEnlevement = elementLivraison.getAttribute("adresseEnlevement");
                    // Integrity check
                    if (adresseEnlevement.isEmpty()) {
                        throw new IllegalStateException("No pickup address found for the delivery request : " + i);
                    }
                    // Check if the pickup address is already in the database, if not throw an exception
                    Optional<Node> nodePickup = nodeRepository.findById(Long.parseLong(adresseEnlevement));
                    if (nodePickup.isEmpty()) {
                        throw new IllegalStateException("The pickup address is not in the map, please load a bigger map including the pickup address");
                    }

                    String adresseLivraison = elementLivraison.getAttribute("adresseLivraison");
                    // Integrity check
                    if (adresseLivraison.isEmpty()) {
                        throw new IllegalStateException("No delivery address found for the delivery request : " + i);
                    }
                    // Check if the delivery address is already in the database, if not throw an exception
                    Optional<Node> nodeDelivery = nodeRepository.findById(Long.parseLong(adresseLivraison));
                    if (nodeDelivery.isEmpty()) {
                        throw new IllegalStateException("The delivery address is not in the map, please load a bigger map including the delivery address");
                    }

                    DeliveryRequest deliveryRequest = new DeliveryRequest();
                    deliveryRequest.setIdDelivery(Long.parseLong(adresseLivraison));
                    deliveryRequest.setIdPickup(Long.parseLong(adresseEnlevement));
                    deliveryRequest.setIdWarehouse(Long.parseLong(warehouseAddress));

                    deliveryRequestRepository.save(deliveryRequest);
                }
            }
        } catch (Exception e) {
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
     *
     * @return The list of nodes
     */
    public Iterable<Node> findAllNodes() {
        return nodeRepository.findAll();
    }

    /**
     * Delete a node by its id
     *
     * @param id The id of the node
     */
    public void deleteNodeById(Long id) {
        nodeRepository.deleteById(id);
    }

    /**
     * Save a node
     *
     * @param node The node to save
     * @return The saved node
     */
    public Node saveNode(Node node) {
        Node savedNode;
        savedNode = nodeRepository.save(node);
        return savedNode;
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
     *
     * @return The list of segments
     */
    public Iterable<Segment> findAllSegments() {
        return segmentRepository.findAll();
    }

    /**
     * Delete a segment by its id
     *
     * @param id The id of the segment
     */
    public void deleteSegmentById(Long id) {
        segmentRepository.deleteById(id);
    }

    /**
     * Save a segment
     *
     * @param segment The segment to save
     * @return The saved segment
     */
    public Segment saveSegment(Segment segment) {
        Segment savedSegment;
        savedSegment = segmentRepository.save(segment);
        return savedSegment;
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
     * @throws IllegalStateException If an error occurs
     */
    public Optional<DeliveryRequest> findDeliveryRequestById(Long id) throws IllegalStateException {
        try {
            return deliveryRequestRepository.findById(id);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Get all delivery requests
     * @return The list of delivery requests
     * @throws IllegalStateException If an error occurs
     */
    public Iterable<DeliveryRequest> findAllDeliveryRequests() throws IllegalStateException {
        try {
            return deliveryRequestRepository.findAll();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Update a delivery request
     * @param id The id of the delivery request
     * @param deliveryRequest The new delivery request
     * @return The updated delivery request
     * @throws IllegalStateException If the delivery request is not found
     */
    public DeliveryRequest updateDeliveryRequest(Long id, DeliveryRequest deliveryRequest) throws IllegalStateException {
        Optional<DeliveryRequest> optionalDeliveryRequest = deliveryRequestRepository.findById(id);
        if (optionalDeliveryRequest.isEmpty()) {
            throw new IllegalStateException("Delivery request not found");
        }
        optionalDeliveryRequest.get().setIdPickup(deliveryRequest.getIdPickup());
        optionalDeliveryRequest.get().setIdDelivery(deliveryRequest.getIdDelivery());
        optionalDeliveryRequest.get().setIdWarehouse(deliveryRequest.getIdWarehouse());

        if (deliveryRequest.getIdCourier() != null) {
            optionalDeliveryRequest.get().setIdCourier(deliveryRequest.getIdCourier());
        }

        return deliveryRequestRepository.save(optionalDeliveryRequest.get());
    }

    /**
     * Delete a delivery request by its id
     * @param id The id of the delivery request
     * @throws IllegalStateException If the delivery request is not found
     */
    public void deleteDeliveryRequestById(Long id) throws IllegalStateException {
        try {
            // Check if the delivery request exists
            Optional<DeliveryRequest> deliveryRequest = deliveryRequestRepository.findById(id);
            if (deliveryRequest.isEmpty()) {
                throw new IllegalStateException("Delivery request not found");
            }
            deliveryRequestRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Save a delivery request
     *
     * @param delivery_request The delivery request to save
     * @return The saved delivery request
     * @throws IllegalStateException If an error occurs
     */
    public DeliveryRequest saveDeliveryRequest(DeliveryRequest delivery_request) throws IllegalStateException {
        try {
            DeliveryRequest savedDeliveryRequest;
            savedDeliveryRequest = deliveryRequestRepository.save(delivery_request);
            return savedDeliveryRequest;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }


    /**
     * Delete all delivery requests
     * @throws IllegalStateException If an error occurs
     */
    public void deleteAllDeliveryRequests() throws IllegalStateException {
        try {
            deliveryRequestRepository.deleteAll();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Get a courier by its id
     *
     * @param id The id of the courier
     * @return The courier
     */
    public Optional<Courier> findCourierById(Long id) throws IllegalStateException {
        try {
            return courierRepository.findById(id);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Get all couriers
     *
     * @return The list of couriers
     */
    public Iterable<Courier> findAllCouriers() throws IllegalStateException {
        try {
            return courierRepository.findAll();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Delete a courier by its id
     *
     * @param id The id of the courier
     */
    public void deleteCourierById(Long id) throws IllegalStateException {
        // Check if the courier is assigned to a delivery request, if so, throw an exception
        List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();
        for (DeliveryRequest deliveryRequest : deliveryRequests) {
            if (deliveryRequest.getIdCourier() != null && deliveryRequest.getIdCourier().equals(id)) {
                throw new IllegalStateException("The courier is assigned to a delivery request");
            }
        }
        courierRepository.deleteById(id);
    }

    /**
     * Save a courier
     *
     * @param courier The courier to save
     * @return The saved courier
     */
    public Courier saveCourier(Courier courier) throws IllegalStateException {
        try {
            Courier savedCourier;
            savedCourier = courierRepository.save(courier);
            return savedCourier;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Delete all couriers
     */
    public void deleteAllCouriers() throws IllegalStateException {
        List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();
        for (DeliveryRequest deliveryRequest : deliveryRequests) {
            if (deliveryRequest.getIdCourier() != null) {
                throw new IllegalStateException("A courier is assigned to a delivery request");
            }
        }
        courierRepository.deleteAll();
    }

    /**
     * Add a courier
     */
    public void addCourier() throws IllegalStateException {
        try {
            Courier courier = new Courier();
            courierRepository.save(courier);
            courier.setName("Courier " + courier.getId());
            courierRepository.save(courier);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Delete the last courier
     */
    public void deleteCourier() throws IllegalStateException {
        List<Courier> couriers = (List<Courier>) courierRepository.findAll();
        if (!couriers.isEmpty()) {
            // check if the courier is assigned to a delivery request, if so, throw an exception
            List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();
            for (DeliveryRequest deliveryRequest : deliveryRequests) {
                if (deliveryRequest.getIdCourier() != null && deliveryRequest.getIdCourier().equals(couriers.get(couriers.size() - 1).getId())) {
                    throw new IllegalStateException("The courier is assigned to a delivery request");
                }
            }
            courierRepository.delete(couriers.get(couriers.size() - 1));
        }
    }

    /**
     * Assign a courier to a delivery request
     *
     * @param idCourier         The id of the courier
     * @param idDeliveryRequest The id of the delivery request
     * @return The delivery request
     */
    public void assignCourier(Long idCourier, Long idDeliveryRequest) throws IllegalStateException {
        try {
            Optional<Courier> courier = courierRepository.findById(idCourier);
            if (courier.isEmpty()) {
                throw new IllegalStateException("Courier not found");
            }

            Optional<DeliveryRequest> deliveryRequest = deliveryRequestRepository.findById(idDeliveryRequest);
            if (deliveryRequest.isEmpty()) {
                throw new IllegalStateException("Delivery request not found");
            }

            deliveryRequest.get().setIdCourier(idCourier);
            deliveryRequestRepository.save(deliveryRequest.get());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }


    /**
     * Calculate the optimal route.
     *
     * @return The list of node IDs representing the optimal route.
     */
    public Map<Long, List<Long>> calculateOptimalRoute() throws IllegalStateException {
        // Fetch all delivery requests
        List<DeliveryRequest> deliveryRequests = (List<DeliveryRequest>) deliveryRequestRepository.findAll();

        if (deliveryRequests.isEmpty()) {
            throw new IllegalStateException("No delivery requests found.");
        }

        System.out.println("-----------------------------------------------------------------------------------");
        for (DeliveryRequest deliveryRequest : deliveryRequests) {
            System.out.println("Courier: " + deliveryRequest.getIdCourier());
        }
        System.out.println("-----------------------------------------------------------------------------------");

        // Fetch all couriers
        List<Courier> courierList = (List<Courier>) courierRepository.findAll();

        if (courierList.isEmpty()) {
            throw new IllegalStateException("No couriers found.");
        }

        Map<Long, List<Long>> listeRoutes = new HashMap<>();

        int nbCouriers = courierList.size();

        for (int i = 0; i < nbCouriers; i++) {
            int finalI = i;
            List<DeliveryRequest> deliveryRequestsCourier = deliveryRequests.stream()
                    .filter(deliveryRequest -> deliveryRequest.getIdCourier() != null)
                    .filter(deliveryRequest -> deliveryRequest.getIdCourier().equals(courierList.get(finalI).getId()))
                    .collect(Collectors.toList());


            if (!deliveryRequestsCourier.isEmpty()) {
                Map<Long, Map<Long, Double>> graph = buildGraph();
                validateGraph(graph, deliveryRequestsCourier);

                List<Long> route = findOptimalRoute(graph, deliveryRequestsCourier);
                listeRoutes.put(courierList.get(i).getId(), route);
            }
        }

        // Check if no courier is assigned to any delivery request like
        if (listeRoutes.isEmpty()) {
            throw new IllegalStateException("No courier is assigned to any delivery request");
        }

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

    /**
     * Parse the XML file
     *
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
}
