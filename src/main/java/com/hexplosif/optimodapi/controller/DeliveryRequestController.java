package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.DeliveryRequest;
import com.hexplosif.optimodapi.service.DeliveryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class DeliveryRequestController {

    @Autowired
    private DeliveryRequestService deliveryrequestService;

    /**
     * Create - Add a new deliveryrequest
     * @param deliveryrequest An object deliveryrequest
     * @return The deliveryrequest object saved
     */
    @PostMapping("/deliveryrequest")
    public DeliveryRequest createDeliveryRequest(@RequestBody DeliveryRequest deliveryrequest) {
        return deliveryrequestService.saveDeliveryRequest(deliveryrequest);
    }


    /**
     * Read - Get one deliveryrequest
     * @param id The id of the deliveryrequest
     * @return An DeliveryRequest object full filled
     */
    @GetMapping("/deliveryrequest/{id}")
    public DeliveryRequest getDeliveryRequest(@PathVariable("id") final Long id) {
        Optional<DeliveryRequest> deliveryrequest = deliveryrequestService.findDeliveryRequestById(id);
        if(deliveryrequest.isPresent()) {
            return deliveryrequest.get();
        } else {
            return null;
        }
    }

    /**
     * Read - Get all deliveryrequests
     * @return - An Iterable object of DeliveryRequest full filled
     */
    @GetMapping("/deliveryrequests")
    public Iterable<DeliveryRequest> getDeliveryRequests() {
        return deliveryrequestService.findAllDeliveryRequests();
    }

    /**
     * Update - Update an existing deliveryrequest
     * @param id - The id of the deliveryrequest to update
     * @param deliveryrequest - The deliveryrequest object updated
     * @return
     */
    @PutMapping("/deliveryrequest/{id}")
    public DeliveryRequest updateDeliveryRequest(@PathVariable("id") final Long id, @RequestBody DeliveryRequest deliveryrequest) {
        Optional<DeliveryRequest> e = deliveryrequestService.findDeliveryRequestById(id);
        if(e.isPresent()) {
            DeliveryRequest currentDeliveryRequest = e.get();

            currentDeliveryRequest.setIdDelivery

                    (deliveryrequest.getIdDelivery

                            ());
            currentDeliveryRequest.setIdPickup

                    (deliveryrequest.getIdPickup

                            ());
            currentDeliveryRequest.setIdWarehouse

                    (deliveryrequest.getIdWarehouse

                            ());

            deliveryrequestService.saveDeliveryRequest(currentDeliveryRequest);
            return currentDeliveryRequest;
        } else {
            return null;
        }
    }


    /**
     * Delete - Delete an deliveryrequest
     * @param id - The id of the deliveryrequest to delete
     */
    @DeleteMapping("/deliveryrequest/{id}")
    public void deleteDeliveryRequest(@PathVariable("id") final Long id) {
        deliveryrequestService.deleteDeliveryRequestById(id);
    }
}