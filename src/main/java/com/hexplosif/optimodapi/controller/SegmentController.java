package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.Segment;
import com.hexplosif.optimodapi.service.SegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SegmentController {
    @Autowired
    private SegmentService segmentService;

    public SegmentController() {
    }

    @GetMapping({"/segments"})
    public Iterable<Segment> getSegments() {
        return this.segmentService.findAllSegments();
    }

    @GetMapping({"/segment/{id}"})
    public Segment getSegmentById(@PathVariable("id") final Long id) {
        return (Segment)this.segmentService.findSegmentById(id).orElse(null);
    }
}
