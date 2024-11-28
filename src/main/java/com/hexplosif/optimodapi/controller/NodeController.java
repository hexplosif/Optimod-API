package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.Node;
import com.hexplosif.optimodapi.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {
    @Autowired
    private NodeService nodeService;

    public NodeController() {
    }

    @GetMapping({"/nodes"})
    public Iterable<Node> getNodes() {
        return this.nodeService.findAllNodes();
    }

    @GetMapping({"/node/{id}"})
    public Node getNodeById(Long id) {
        return (Node)this.nodeService.findNodeById(id).orElse(null);
    }
}