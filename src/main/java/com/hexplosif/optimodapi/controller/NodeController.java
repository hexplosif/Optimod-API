package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.Node;
import com.hexplosif.optimodapi.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class NodeController {

    @Autowired
    private NodeService nodeService;

    /**
     * Create - Add a new node
     * @param node An object node
     * @return The node object saved
     */
    @PostMapping("/node")
    public Node createNode(@RequestBody Node node) {
        return nodeService.saveNode(node);
    }


    /**
     * Read - Get one node
     * @param id The id of the node
     * @return An Node object full filled
     */
    @GetMapping("/node/{id}")
    public Node getNode(@PathVariable("id") final Long id) {
        Optional<Node> node = nodeService.findNodeById(id);
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
        return nodeService.findAllNodes();
    }

    /**
     * Update - Update an existing node
     * @param id - The id of the node to update
     * @param node - The node object updated
     * @return
     */
    @PutMapping("/node/{id}")
    public Node updateNode(@PathVariable("id") final Long id, @RequestBody Node node) {
        Optional<Node> e = nodeService.findNodeById(id);
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

            nodeService.saveNode(currentNode);
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
        nodeService.deleteNodeById(id);
    }
}