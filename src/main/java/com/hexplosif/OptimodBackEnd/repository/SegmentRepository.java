package com.hexplosif.OptimodBackEnd.repository;

import com.hexplosif.OptimodBackEnd.model.Segment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends CrudRepository<Segment, Long> {
}