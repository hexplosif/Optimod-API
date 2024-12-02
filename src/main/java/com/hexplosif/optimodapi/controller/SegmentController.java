package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.Segment;
import com.hexplosif.optimodapi.service.SegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class SegmentController {

    @Autowired
    private SegmentService segmentService;

    /**
     * Create - Add a new segment
     * @param segment An object segment
     * @return The segment object saved
     */
    @PostMapping("/segment")
    public Segment createSegment(@RequestBody Segment segment) {
        return segmentService.saveSegment(segment);
    }

    /**
     * Create - Add new segments
     * @param segments An iterable object of segment
     * @return An Iterable object of Segment object saved
     */
    @PostMapping("/segments")
    public Iterable<Segment> createSegments(@RequestBody Iterable<Segment> segments) {
        return segmentService.saveSegments(segments);
    }

    /**
     * Read - Get one segment
     * @param id The id of the segment
     * @return An Segment object full filled
     */
    @GetMapping("/segment/{id}")
    public Segment getSegment(@PathVariable("id") final Long id) {
        Optional<Segment> segment = segmentService.findSegmentById(id);
        if(segment.isPresent()) {
            return segment.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all segments
     * @return - An Iterable object of Segment full filled
     */
    @GetMapping("/segments")
    public Iterable<Segment> getSegments() {
        return segmentService.findAllSegments();
    }

    /**
     * Delete - Delete all segments
     */
    @DeleteMapping("/segments")
    public void deleteSegments() {
        segmentService.deleteAllSegments();
    }

    /**
     * Update - Update an existing segment
     * @param id - The id of the segment to update
     * @param segment - The segment object updated
     * @return
     */
    @PutMapping("/segment/{id}")
    public Segment updateSegment(@PathVariable("id") final Long id, @RequestBody Segment segment) {
        Optional<Segment> e = segmentService.findSegmentById(id);
        if(e.isPresent()) {
            Segment currentSegment = e.get();

            Long idOrigin = segment.getIdOrigin();
            if(idOrigin != null) {
                currentSegment.setIdOrigin(idOrigin);
            }

            Long idDestination = segment.getIdDestination();
            if(idDestination != null) {
                currentSegment.setIdDestination(idDestination);
            }

            Double length = segment.getLength();
            if(length != null) {
                currentSegment.setLength(length);
            }

            String name = segment.getName();
            if(name != null) {
                currentSegment.setName(name);
            }

            segmentService.saveSegment(currentSegment);
            return currentSegment;
        } else {
            return null;
        }
    }


    /**
     * Delete - Delete an segment
     * @param id - The id of the segment to delete
     */
    @DeleteMapping("/segment/{id}")
    public void deleteSegment(@PathVariable("id") final Long id) {
        segmentService.deleteSegmentById(id);
    }
}