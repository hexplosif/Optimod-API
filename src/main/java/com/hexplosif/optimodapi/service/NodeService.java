package com.hexplosif.optimodapi.service;

import com.hexplosif.optimodapi.model.Node;
import com.hexplosif.optimodapi.repository.NodeRepository;
import java.util.Optional;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeService {
    @Autowired
    private NodeRepository nodeRepository;

    public Optional<Node> findNodeById(final Long id) {
        return this.nodeRepository.findById(id);
    }

    public Node saveNode(final Node node) {
        return (Node) this.nodeRepository.save(node);
    }

    public void deleteNodeById(final Long id) {
        this.nodeRepository.deleteById(id);
    }

    public Iterable<Node> findAllNodes() {
        return this.nodeRepository.findAll();
    }

    public void deleteAllNodes() {
        this.nodeRepository.deleteAll();
    }

    public long countNodes() {
        return this.nodeRepository.count();
    }
}