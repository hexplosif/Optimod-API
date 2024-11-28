package com.hexplosif.optimodapi.repository;

import com.hexplosif.optimodapi.model.Segment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends CrudRepository<Segment, Long> {
}