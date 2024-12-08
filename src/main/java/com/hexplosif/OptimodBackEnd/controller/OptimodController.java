package com.hexplosif.OptimodBackEnd.controller;

import com.hexplosif.OptimodBackEnd.model.Courier;
import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import com.hexplosif.OptimodBackEnd.model.Node;
import com.hexplosif.OptimodBackEnd.model.Segment;
import com.hexplosif.OptimodBackEnd.service.OptimodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class OptimodController {

    @Autowired
    private OptimodService optimodService;

    @PostMapping("/loadMap")
    public ResponseEntity<Map<String, Object>> loadMap(@RequestParam("file") MultipartFile file) {
        try {
            String XMLFileName = saveUploadedFile(file);

            // Supprimer les données existantes
            optimodService.deleteAllNodes();
            optimodService.deleteAllSegments();

            // Charger les données
            optimodService.loadNode(XMLFileName);
            optimodService.loadSegment(XMLFileName);

            // Renvoyer les données mises à jour
            Map<String, Object> response = new HashMap<>();
            response.put("nodes", optimodService.findAllNodes()); // Renvoie-les nodes
            response.put("segments", optimodService.findAllSegments()); // Renvoie les segments

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erreur lors du chargement de la carte.",
                    "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/loadDeliveryRequest")
    public ResponseEntity<Map<String, Object>> loadDeliveryRequest(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file to a temporary location
            String XMLFileName = saveUploadedFile(file);

            // Load delivery requests into the database
            optimodService.loadDeliveryRequest(XMLFileName);

            // Calculate the optimal route
            List<Long> optimalRoute = optimodService.calculateOptimalRoute();

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("deliveryRequests", optimodService.findAllDeliveryRequests());
            response.put("optimalRoute", optimalRoute);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erreur lors du chargement des demandes de livraison.",
                    "details", e.getMessage()
            ));
        }
    }

    /**
     * Create - Add a new node
     * @param node An object node
     * @return The node object saved
     */
    @PostMapping("/node")
    public Node createNode(@RequestBody Node node) {
        return optimodService.saveNode(node);
    }

    /**
     * Create - Add new nodes
     * @param nodes An iterable object of node
     * @return An Iterable object of Node object saved
     */
    @PostMapping("/nodes")
    public Iterable<Node> createNodes(@RequestBody Iterable<Node> nodes) {
        return optimodService.createNodes(nodes);
    }

    /**
     * Read - Get one node
     * @param id The id of the node
     * @return A Node object fulfilled
     */
    @GetMapping("/node/{id}")
    public Node getNode(@PathVariable("id") final Long id) {
        Optional<Node> node = optimodService.findNodeById(id);
        if(node.isPresent()) {
            return node.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all nodes
     * @return - An Iterable object of Node fulfilled
     */
    @GetMapping("/nodes")
    public Iterable<Node> getNodes() {
        return optimodService.findAllNodes();
    }

    /**
     * Delete - Delete all nodes
     */
    @DeleteMapping("/nodes")
    public void deleteNodes() {
        optimodService.deleteAllNodes();
    }

    /**
     * Update - Update an existing node
     * @param id - The id of the node to update
     * @param node - The node object updated
     * @return The Node object updated
     */
    @PutMapping("/node/{id}")
    public Node updateNode(@PathVariable("id") final Long id, @RequestBody Node node) {
        Optional<Node> e = optimodService.findNodeById(id);
        if(e.isPresent()) {
            Node currentNode = e.get();

            Double latitude = node.getLatitude();
            if(latitude != null) {
                currentNode.setLatitude(latitude);
            }

            Double longitude = node.getLongitude();
            if(longitude != null) {
                currentNode.setLongitude(longitude);
            }

            optimodService.saveNode(currentNode);
            return currentNode;
        } else {
            return null;
        }
    }


    /**
     * Delete - Delete an node
     * @param id - The id of the node to delete
     */
    @DeleteMapping("/node/{id}")
    public void deleteNode(@PathVariable("id") final Long id) {
        optimodService.deleteNodeById(id);
    }

    /**
     * Create - Add a new segment
     * @param segment An object segment
     * @return The segment object saved
     */
    @PostMapping("/segment")
    public Segment createSegment(@RequestBody Segment segment) {
        return optimodService.saveSegment(segment);
    }

    /**
     * Create - Add new segments
     * @param segments An iterable object of segment
     * @return An Iterable object of Segment object saved
     */
    @PostMapping("/segments")
    public Iterable<Segment> createSegments(@RequestBody Iterable<Segment> segments) {
        return optimodService.createSegments(segments);
    }

    /**
     * Read - Get one segment
     * @param id The id of the segment
     * @return A Segment object fulfilled
     */
    @GetMapping("/segment/{id}")
    public Segment getSegment(@PathVariable("id") final Long id) {
        Optional<Segment> segment = optimodService.findSegmentById(id);
        if(segment.isPresent()) {
            return segment.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all segments
     * @return - An Iterable object of Segment fulfilled
     */
    @GetMapping("/segments")
    public Iterable<Segment> getSegments() {
        return optimodService.findAllSegments();
    }

    /**
     * Delete - Delete all segments
     */
    @DeleteMapping("/segments")
    public void deleteSegments() {
        optimodService.deleteAllSegments();
    }

    /**
     * Update - Update an existing segment
     * @param id - The id of the segment to update
     * @param segment - The segment object updated
     * @return The Segment object updated
     */
    @PutMapping("/segment/{id}")
    public Segment updateSegment(@PathVariable("id") final Long id, @RequestBody Segment segment) {
        Optional<Segment> e = optimodService.findSegmentById(id);
        if(e.isPresent()) {
            Segment currentSegment = e.get();

            Long idOrigin = segment.getIdOrigin();
            if(idOrigin != null) {
                currentSegment.setIdOrigin(idOrigin);
            }

            Long idDestination = segment.getIdDestination();
            if(idDestination != null) {
                currentSegment.setIdDestination(idDestination);
            }

            Double length = segment.getLength();
            if(length != null) {
                currentSegment.setLength(length);
            }

            String name = segment.getName();
            if(name != null) {
                currentSegment.setName(name);
            }

            optimodService.saveSegment(currentSegment);
            return currentSegment;
        } else {
            return null;
        }
    }

    /**
     * Delete - Delete an segment
     * @param id - The id of the segment to delete
     */
    @DeleteMapping("/segment/{id}")
    public void deleteSegment(@PathVariable("id") final Long id) {
        optimodService.deleteSegmentById(id);
    }

    /**
     * Create - Add a new delivery_request
     * @param delivery_request An object delivery_request
     * @return The delivery_request object saved
     */
    @PostMapping("/delivery_request")
    public DeliveryRequest createDeliveryRequest(@RequestBody DeliveryRequest delivery_request) {
        return optimodService.saveDeliveryRequest(delivery_request);
    }

    /**
     * Read - Get one delivery_request
     * @param id The id of the delivery_request
     * @return An DeliveryRequest object fulfilled
     */
    @GetMapping("/delivery_request/{id}")
    public DeliveryRequest getDeliveryRequest(@PathVariable("id") final Long id) {
        Optional<DeliveryRequest> delivery_request = optimodService.findDeliveryRequestById(id);
        if(delivery_request.isPresent()) {
            return delivery_request.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all delivery_requests
     * @return - An Iterable object of DeliveryRequest fulfilled
     */
    @GetMapping("/delivery_requests")
    public Iterable<DeliveryRequest> getDeliveryRequests() {
        return optimodService.findAllDeliveryRequests();
    }

    /**
     * Delete - Delete all delivery_requests
     */
    @DeleteMapping("/delivery_requests")
    public void deleteDeliveryRequests() {
        optimodService.deleteAllDeliveryRequests();
    }

    /**
     * Update - Update an existing delivery_request
     * @param id - The id of the delivery_request to update
     * @param delivery_request - The delivery_request object updated
     * @return The DeliveryRequest object updated
     */
    @PutMapping("/delivery_request/{id}")
    public DeliveryRequest updateDeliveryRequest(@PathVariable("id") final Long id, @RequestBody DeliveryRequest delivery_request) {
        Optional<DeliveryRequest> e = optimodService.findDeliveryRequestById(id);
        if(e.isPresent()) {
            DeliveryRequest currentDeliveryRequest = e.get();

            currentDeliveryRequest.setIdDelivery(delivery_request.getIdDelivery());
            currentDeliveryRequest.setIdPickup(delivery_request.getIdPickup());
            currentDeliveryRequest.setIdWarehouse(delivery_request.getIdWarehouse());

            optimodService.saveDeliveryRequest(currentDeliveryRequest);
            return currentDeliveryRequest;
        } else {
            return null;
        }
    }

    /**
     * Delete - Delete an delivery_request
     * @param id - The id of the delivery_request to delete
     */
    @DeleteMapping("/delivery_request/{id}")
    public void deleteDeliveryRequest(@PathVariable("id") final Long id) {
        optimodService.deleteDeliveryRequestById(id);
    }

    /**
     * Create - Add a new courier
     * @param courier An object courier
     * @return The courier object saved
     */
    @PostMapping("/courier")
    public Courier createCourier(@RequestBody Courier courier) {
        return optimodService.saveCourier(courier);
    }

    /**
     * Read - Get one courier
     * @param id The id of the courier
     * @return An Courier object fulfilled
     */
    @GetMapping("/courier/{id}")
    public Courier getCourier(@PathVariable("id") final Long id) {
        Optional<Courier> courier = optimodService.findCourierById(id);
        if(courier.isPresent()) {
            return courier.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all couriers
     * @return - An Iterable object of Courier fulfilled
     */
    @GetMapping("/couriers")
    public Iterable<Courier> getCouriers() {
        return optimodService.findAllCouriers();
    }

    /**
     * Delete - Delete all couriers
     */
    @DeleteMapping("/couriers")
    public void deleteCouriers() {
        optimodService.deleteAllCouriers();
    }

    /**
     * Update - Update an existing courier
     * @param id - The id of the courier to update
     * @param courier - The courier object updated
     * @return The Courier object updated
     */
    @PutMapping("/courier/{id}")
    public Courier updateCourier(@PathVariable("id") final Long id, @RequestBody Courier courier) {
        Optional<Courier> e = optimodService.findCourierById(id);
        if(e.isPresent()) {
            Courier currentCourier = e.get();

            currentCourier.setName(courier.getName());

            optimodService.saveCourier(currentCourier);
            return currentCourier;
        } else {
            return null;
        }
    }

    /**
     * Delete - Delete a courier
     * @param id - The id of the courier to delete
     */
    @DeleteMapping("/courier/{id}")
    public void deleteCourier(@PathVariable("id") final Long id) {
        optimodService.deleteCourierById(id);
    }

    /**
     * Assign a courier to a delivery request
     * @param body A map containing the courier id and the delivery request id
     * @return The DeliveryRequest object updated
     */
    @PutMapping("/assignCourier")
    public DeliveryRequest assignCourier(@RequestBody Map<String, Long> body) {
        Long idCourier = body.get("courierId");
        Long idDeliveryRequest = body.get("deliveryRequestId");
        System.out.println("Assigning courier " + idCourier + " to delivery request " + idDeliveryRequest);
        return optimodService.assignCourier(idCourier, idDeliveryRequest);
    }

    /**
     * Calculate the optimal route
     * @return A list of node ids representing the optimal route
     */
    @GetMapping("/calculateOptimalRoute")
    public List<Long> calculateOptimalRoute() throws Exception {
        System.out.println("Calculating optimal route");
        return optimodService.calculateOptimalRoute();
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String tempFileName = System.getProperty("java.io.tmpdir") + file.getOriginalFilename();
        file.transferTo(new java.io.File(tempFileName));
        return tempFileName;
    }
}
