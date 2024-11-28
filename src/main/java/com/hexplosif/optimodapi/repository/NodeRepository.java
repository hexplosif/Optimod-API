package com.hexplosif.optimodapi.repository;

import com.hexplosif.optimodapi.model.Node;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends CrudRepository<Node, Long> {
}