package com.hexplosif.OptimodBackEnd.controller;

import com.hexplosif.OptimodBackEnd.model.Courier;
import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import com.hexplosif.OptimodBackEnd.model.Node;
import com.hexplosif.OptimodBackEnd.model.Segment;
import com.hexplosif.OptimodBackEnd.service.OptimodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class OptimodController {

    @Autowired
    private OptimodService optimodService;

    /**
     * Load a map from an XML file
     *
     * @param file - The XML file containing the map
     * @return A ResponseEntity object containing the map data
     * 200 OK if the map is loaded successfully
     * 400 Bad Request if an error occurs while loading the map
     * 500 Internal Server Error if an error occurs
     */
    @PostMapping("/loadMap")
    public ResponseEntity<String> loadMap(@RequestParam("file") MultipartFile file) {
        try {
            String XMLFileName = saveUploadedFile(file);

            // Supprimer les données existantes
            optimodService.deleteAllNodes();
            optimodService.deleteAllSegments();

            // Charger les données
            optimodService.loadNode(XMLFileName);
            optimodService.loadSegment(XMLFileName);

            return ResponseEntity.ok("Carte chargée avec succès.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * Load a delivery request from an XML file
     *
     * @param file - The XML file containing the delivery request
     * @return A ResponseEntity object containing the delivery request data
     * 200 OK if the delivery request is loaded successfully
     * 400 Bad Request if an error occurs while loading the delivery request
     * 500 Internal Server Error if an error occurs
     */
    @PostMapping("/loadDeliveryRequest")
    public ResponseEntity<String> loadDeliveryRequest(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file to a temporary location
            String XMLFileName = saveUploadedFile(file);

            // Load delivery requests into the database
            optimodService.loadDeliveryRequest(XMLFileName);

            return ResponseEntity.ok("Demande de livraison chargée avec succès.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    /**
     * Create - Add a new node
     *
     * @param node An object node
     * @return The node object saved
     */
    @PostMapping("/node")
    public Node createNode(@RequestBody Node node) {
        return optimodService.saveNode(node);
    }

    /**
     * Create - Add new nodes
     *
     * @param nodes An iterable object of node
     * @return An Iterable object of Node object saved
     */
    @PostMapping("/nodes")
    public Iterable<Node> createNodes(@RequestBody Iterable<Node> nodes) {
        return optimodService.createNodes(nodes);
    }

    /**
     * Read - Get one node
     *
     * @param id The id of the node
     * @return A Node object fulfilled
     */
    @GetMapping("/node/{id}")
    public Node getNode(@PathVariable("id") final Long id) {
        Optional<Node> node = optimodService.findNodeById(id);
        return node.orElse(null);
    }

    /**
     * Read - Get all nodes
     *
     * @return - An Iterable object of Node fulfilled
     */
    @GetMapping("/nodes")
    public Iterable<Node> getNodes() {
        return optimodService.findAllNodes();
    }

    /**
     * Delete - Delete all nodes
     * @return A ResponseEntity object containing the result of the deletion
     *    204 No Content if deletion is successful
     *    500 Internal Server Error if an error occurs
     */
    @DeleteMapping("/nodes")
    public ResponseEntity<String> deleteNodes() {
        try {
            optimodService.deleteAllNodes();
            return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Update - Update an existing node
     *
     * @param id   - The id of the node to update
     * @param node - The node object updated
     * @return The Node object updated
     */
    @PutMapping("/node/{id}")
    public Node updateNode(@PathVariable("id") final Long id, @RequestBody Node node) {
        Optional<Node> e = optimodService.findNodeById(id);
        if (e.isPresent()) {
            Node currentNode = e.get();

            Double latitude = node.getLatitude();
            if (latitude != null) {
                currentNode.setLatitude(latitude);
            }

            Double longitude = node.getLongitude();
            if (longitude != null) {
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
     *
     * @param id - The id of the node to delete
     */
    @DeleteMapping("/node/{id}")
    public void deleteNode(@PathVariable("id") final Long id) {
        optimodService.deleteNodeById(id);
    }

    /**
     * Create - Add a new segment
     *
     * @param segment An object segment
     * @return The segment object saved
     */
    @PostMapping("/segment")
    public Segment createSegment(@RequestBody Segment segment) {
        return optimodService.saveSegment(segment);
    }

    /**
     * Create - Add new segments
     *
     * @param segments An iterable object of segment
     * @return An Iterable object of Segment object saved
     */
    @PostMapping("/segments")
    public Iterable<Segment> createSegments(@RequestBody Iterable<Segment> segments) {
        return optimodService.createSegments(segments);
    }

    /**
     * Read - Get one segment
     *
     * @param id The id of the segment
     * @return A Segment object fulfilled
     */
    @GetMapping("/segment/{id}")
    public Segment getSegment(@PathVariable("id") final Long id) {
        Optional<Segment> segment = optimodService.findSegmentById(id);
        return segment.orElse(null);
    }

    /**
     * Read - Get all segments
     *
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
     *
     * @param id      - The id of the segment to update
     * @param segment - The segment object updated
     * @return The Segment object updated
     */
    @PutMapping("/segment/{id}")
    public Segment updateSegment(@PathVariable("id") final Long id, @RequestBody Segment segment) {
        Optional<Segment> e = optimodService.findSegmentById(id);
        if (e.isPresent()) {
            Segment currentSegment = e.get();

            Long idOrigin = segment.getIdOrigin();
            if (idOrigin != null) {
                currentSegment.setIdOrigin(idOrigin);
            }

            Long idDestination = segment.getIdDestination();
            if (idDestination != null) {
                currentSegment.setIdDestination(idDestination);
            }

            Double length = segment.getLength();
            if (length != null) {
                currentSegment.setLength(length);
            }

            String name = segment.getName();
            if (name != null) {
                currentSegment.setName(name);
            }

            optimodService.saveSegment(currentSegment);
            return currentSegment;
        } else {
            return null;
        }
    }

    /**
     * Delete - Delete a segment
     *
     * @param id - The id of the segment to delete
     */
    @DeleteMapping("/segment/{id}")
    public void deleteSegment(@PathVariable("id") final Long id) {
        optimodService.deleteSegmentById(id);
    }

    /**
     * Create - Add a new delivery_request
     *
     * @param delivery_request An object delivery_request
     * @return A ResponseEntity object containing the delivery_request object saved
     * 200 OK if the delivery_request is saved successfully
     * 500 Internal Server Error if an error occurs
     */
    @PostMapping("/delivery_request")
    public ResponseEntity<DeliveryRequest> createDeliveryRequest(@RequestBody DeliveryRequest delivery_request) {
        try {
            return ResponseEntity.ok(optimodService.saveDeliveryRequest(delivery_request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Read - Get one delivery_request
     *
     * @param id The id of the delivery_request
     * @return A ResponseEntity object containing the delivery_request object
     * 200 OK if the delivery_request is fetched successfully
     * 500 Internal Server Error if an error occurs
     */
    @GetMapping("/delivery_request/{id}")
    public ResponseEntity<Optional<DeliveryRequest>> getDeliveryRequest(@PathVariable("id") final Long id) {
        try {
            return ResponseEntity.ok(optimodService.findDeliveryRequestById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Read - Get all delivery_requests
     *
     * @return - A ResponseEntity object containing the list of delivery_requests
     * 200 OK if the delivery_requests are fetched successfully
     * 500 Internal Server Error if an error occurs
     */
    @GetMapping("/delivery_requests")
    public ResponseEntity<Iterable<DeliveryRequest>> getDeliveryRequests() {
        try {
            return ResponseEntity.ok(optimodService.findAllDeliveryRequests());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Delete - Delete all delivery_requests
     *
     * @return A ResponseEntity object containing the result of the deletion
     * 204 No Content if deletion is successful
     * 500 Internal Server Error if an error occurs
     */
    @DeleteMapping("/delivery_requests")
    public ResponseEntity<String> deleteDeliveryRequests() {
        try {
            optimodService.deleteAllDeliveryRequests();
            return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Update - Update an existing delivery_request
     *
     * @param id - The id of the delivery_request to update
     *           delivery_request - The delivery_request object updated
     * @return A ResponseEntity object containing the delivery_request object updated
     * 200 OK if the delivery_request is updated successfully
     * 500 Internal Server Error if an error occurs
     */
    @PutMapping("/delivery_request/{id}")
    public ResponseEntity<DeliveryRequest> updateDeliveryRequest(@PathVariable("id") final Long id, @RequestBody DeliveryRequest delivery_request) {
        try {
            return ResponseEntity.ok(optimodService.updateDeliveryRequest(id, delivery_request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Delete - Delete an delivery_request
     *
     * @param id - The id of the delivery_request to delete
     * @return A ResponseEntity object containing the result of the deletion
     * 204 No Content if deletion is successful
     * 400 Bad Request if deletion is not possible
     * 500 Internal Server Error if an error occurs
     */
    @DeleteMapping("/delivery_request/{id}")
    public ResponseEntity<String> deleteDeliveryRequest(@PathVariable("id") final Long id) {
        try {
            optimodService.deleteDeliveryRequestById(id);
            return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
        } catch (IllegalStateException e) {
            // Renvoyer une erreur 400 (Bad Request) avec le message d'erreur
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Create - Add a new courier
     *
     * @param courier An object courier
     * @return A ResponseEntity object containing the courier object saved
     * 200 OK if the courier is saved successfully
     * 500 Internal Server Error if an error occurs
     */
    @PostMapping("/courier")
    public ResponseEntity<Courier> createCourier(@RequestBody Courier courier) {
        try {
            return ResponseEntity.ok(optimodService.saveCourier(courier));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Read - Get one courier
     *
     * @param id The id of the courier
     * @return A Courier object fulfilled
     */
    @GetMapping("/courier/{id}")
    public ResponseEntity<Optional<Courier>> getCourier(@PathVariable("id") final Long id) {
        try {
            return ResponseEntity.ok(optimodService.findCourierById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Read - Get all couriers
     *
     * @return - A ResponseEntity object containing the list of couriers
     */
    @GetMapping("/couriers")
    public ResponseEntity<Iterable<Courier>> getCouriers() {
        try {
            return ResponseEntity.ok(optimodService.findAllCouriers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Delete - Delete all couriers
     *
     * @return A ResponseEntity object containing the result of the deletion
     * 204 No Content if deletion is successful
     * 500 Internal Server Error if an error occurs
     */
    @DeleteMapping("/couriers")
    public ResponseEntity<String> deleteCouriers() {
        try {
            optimodService.deleteAllCouriers();
            return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }


    /**
     * Delete - Delete a courier
     *
     * @param id - The id of the courier to delete
     * @return A ResponseEntity object containing the result of the deletion
     * 204 No Content if deletion is successful
     * 400 Bad Request if deletion is not possible
     * 500 Internal Server Error if an error occurs
     */
    @DeleteMapping("/courier/{id}")
    public ResponseEntity<String> deleteCourier(@PathVariable("id") final Long id) {
        try {
            System.out.println("Deleting courier " + id);
            optimodService.deleteCourierById(id);
            return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
        } catch (IllegalStateException e) {
            // Renvoyer une erreur 400 (Bad Request) avec le message d'erreur
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Add a courier
     *
     * @return A ResponseEntity object containing the result of the addition
     * 204 No Content if addition is successful
     * 500 Internal Server Error if an error occurs
     */
    @PostMapping("/addCourier")
    public ResponseEntity<String> addCourier() {
        try {
            optimodService.addCourier();
            return ResponseEntity.noContent().build(); // 204 No Content si ajout réussi
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Delete the last courier
     *
     * @return A ResponseEntity object containing the result of the deletion
     * 204 No Content if deletion is successful
     * 400 Bad Request if deletion is not possible
     * 500 Internal Server Error if an error occurs
     */
    @DeleteMapping("/deleteCourier")
    public ResponseEntity<String> deleteCourier() {
        try {
            optimodService.deleteCourier();
            return ResponseEntity.noContent().build(); // 204 No Content si suppression réussie
        } catch (IllegalStateException e) {
            // Renvoyer une erreur 400 (Bad Request) avec le message d'erreur
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Assign a courier to a delivery request
     * @param body A map containing the courier id and the delivery request id
     * @return A ResponseEntity object containing the result of the assignment
     * 204 No Content if assignment is successful
     * 400 Bad Request if assignment is not possible
     * 500 Internal Server Error if an error occurs
     */
    @PutMapping("/assignCourier")
    public ResponseEntity<String> assignCourier(@RequestBody Map<String, Long> body) {
        try {
            Long courierId = body.get("courierId");
            Long deliveryRequestId = body.get("deliveryRequestId");

            optimodService.assignCourier(courierId, deliveryRequestId);
            return ResponseEntity.noContent().build(); // 204 No Content si assignation réussie
        } catch (IllegalStateException e) {
            // Renvoyer une erreur 400 (Bad Request) avec le message d'erreur
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * Calculate the optimal route
     *
     * @return A list of list of Long containing the optimal route
     * The first list contains the ids of the couriers
     * The following lists contain the ids of the delivery requests for each courier
     * @throws IllegalStateException If the optimal route cannot be calculated
     */
    @GetMapping("/calculateOptimalRoute")
    public ResponseEntity<?> calculateOptimalRoute() {
        try {
            Map<Long, List<Long>> optimalRoute = optimodService.calculateOptimalRoute();
            return ResponseEntity.ok(optimalRoute);
        } catch (IllegalStateException e) {
            // Renvoyer une erreur 400 (Bad Request) avec le message d'erreur
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Renvoyer une erreur générique 500 (Internal Server Error)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur.");
        }
    }

    /**
     * @param file The file to save
     * @return tmpFileName The name of the temporary file
     * @throws IOException If an error occurs while saving the file
     */
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String tempFileName = System.getProperty("java.io.tmpdir") + file.getOriginalFilename();
        file.transferTo(new java.io.File(tempFileName));
        return tempFileName;
    }
}
