package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.DeliveryRequest;
import com.hexplosif.optimodapi.model.Node;
import com.hexplosif.optimodapi.model.Segment;
import com.hexplosif.optimodapi.service.OptimodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
            response.put("nodes", optimodService.findAllNodes()); // Renvoie les nodes
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
            String XMLFileName = saveUploadedFile(file);

            // Supprimer les données existantes
            optimodService.deleteAllDeliveryRequests();

            // Charger les données
            optimodService.loadDeliveryRequest(XMLFileName);

            // Renvoyer les données mises à jour
            Map<String, Object> response = new HashMap<>();
            response.put("deliveryrequests", optimodService.findAllDeliveryRequests()); // Renvoie les demandes de livraisons

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erreur lors du chargement de la carte.",
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
     * @return An Node object full filled
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
     * @return - An Iterable object of Node full filled
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
     * @return
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
     * @return An Segment object full filled
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
     * @return - An Iterable object of Segment full filled
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
     * @return
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
     * Create - Add a new deliveryrequest
     * @param deliveryrequest An object deliveryrequest
     * @return The deliveryrequest object saved
     */
    @PostMapping("/deliveryrequest")
    public DeliveryRequest createDeliveryRequest(@RequestBody DeliveryRequest deliveryrequest) {
        return optimodService.saveDeliveryRequest(deliveryrequest);
    }

    /**
     * Read - Get one deliveryrequest
     * @param id The id of the deliveryrequest
     * @return An DeliveryRequest object full filled
     */
    @GetMapping("/deliveryrequest/{id}")
    public DeliveryRequest getDeliveryRequest(@PathVariable("id") final Long id) {
        Optional<DeliveryRequest> deliveryrequest = optimodService.findDeliveryRequestById(id);
        if(deliveryrequest.isPresent()) {
            return deliveryrequest.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all deliveryrequests
     * @return - An Iterable object of DeliveryRequest full filled
     */
    @GetMapping("/deliveryrequests")
    public Iterable<DeliveryRequest> getDeliveryRequests() {
        return optimodService.findAllDeliveryRequests();
    }

    /**
     * Delete - Delete all deliveryrequests
     */
    @DeleteMapping("/deliveryrequests")
    public void deleteDeliveryRequests() {
        optimodService.deleteAllDeliveryRequests();
    }

    /**
     * Update - Update an existing deliveryrequest
     * @param id - The id of the deliveryrequest to update
     * @param deliveryrequest - The deliveryrequest object updated
     * @return
     */
    @PutMapping("/deliveryrequest/{id}")
    public DeliveryRequest updateDeliveryRequest(@PathVariable("id") final Long id, @RequestBody DeliveryRequest deliveryrequest) {
        Optional<DeliveryRequest> e = optimodService.findDeliveryRequestById(id);
        if(e.isPresent()) {
            DeliveryRequest currentDeliveryRequest = e.get();

            currentDeliveryRequest.setIdDelivery(deliveryrequest.getIdDelivery());
            currentDeliveryRequest.setIdPickup(deliveryrequest.getIdPickup());
            currentDeliveryRequest.setIdWarehouse(deliveryrequest.getIdWarehouse());

            optimodService.saveDeliveryRequest(currentDeliveryRequest);
            return currentDeliveryRequest;
        } else {
            return null;
        }
    }


    /**
     * Delete - Delete an deliveryrequest
     * @param id - The id of the deliveryrequest to delete
     */
    @DeleteMapping("/deliveryrequest/{id}")
    public void deleteDeliveryRequest(@PathVariable("id") final Long id) {
        optimodService.deleteDeliveryRequestById(id);
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        String tempFileName = System.getProperty("java.io.tmpdir") + file.getOriginalFilename();
        file.transferTo(new java.io.File(tempFileName));
        return tempFileName;
    }
}
