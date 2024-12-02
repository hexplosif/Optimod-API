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
    private SegmentRepository segmentRepository;

    public Optional<Segment> findSegmentById(final Long id) {
        return this.segmentRepository.findById(id);
    }

    public Segment saveSegment(final Segment segment) {
        return (Segment) this.segmentRepository.save(segment);
    }

    public void deleteSegmentById(final Long id) {
        this.segmentRepository.deleteById(id);
    }

    public Iterable<Segment> findAllSegments() {
        return this.segmentRepository.findAll();
    }

    public void deleteAllSegments() {
        this.segmentRepository.deleteAll();
    }

    public long countSegments() {
        return this.segmentRepository.count();
    }

    public Iterable<Segment> saveSegments(Iterable<Segment> segments) {
        return this.segmentRepository.saveAll(segments);
    }
}