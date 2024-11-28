package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.Node;
import com.hexplosif.optimodapi.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {
    @Autowired
    private NodeService nodeService;

    public NodeController() {
    }

    /**
     * Get all nodes
     * @return Iterable<Node>
     */
    @GetMapping({"/nodes"})
    public Iterable<Node> getNodes() {
        return this.nodeService.findAllNodes();
    }

    /**
     * Get node by id
     * @param id
     * @return Node
     */
    @GetMapping({"/node/{id}"})
    public Node getNodeById(@PathVariable("id") final Long id) {
        return (Node)this.nodeService.findNodeById(id).orElse(null);
    }
}