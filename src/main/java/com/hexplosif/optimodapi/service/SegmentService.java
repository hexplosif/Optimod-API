package com.hexplosif.optimodapi.service;

import com.hexplosif.optimodapi.model.Segment;
import com.hexplosif.optimodapi.repository.SegmentRepository;
import java.util.Optional;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegmentService {
    @Autowired
    private SegmentRepository nodeRepository;

    public Optional<Segment> findSegmentById(final Long id) {
        return this.nodeRepository.findById(id);
    }

    public Segment saveSegment(final Segment node) {
        return (Segment) this.nodeRepository.save(node);
    }

    public void deleteSegmentById(final Long id) {
        this.nodeRepository.deleteById(id);
    }

    public Iterable<Segment> findAllSegments() {
        return this.nodeRepository.findAll();
    }

    public void deleteAllSegments() {
        this.nodeRepository.deleteAll();
    }

    public long countSegments() {
        return this.nodeRepository.count();
    }
}