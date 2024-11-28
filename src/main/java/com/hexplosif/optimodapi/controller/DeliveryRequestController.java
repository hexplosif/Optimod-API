package com.hexplosif.optimodapi.controller;

import com.hexplosif.optimodapi.model.DeliveryRequest;
import com.hexplosif.optimodapi.service.DeliveryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryRequestController {
    @Autowired
    private DeliveryRequestService DeliveryRequestService;

    public DeliveryRequestController() {
    }

    @GetMapping({"/deliveryrequests"})
    public Iterable<DeliveryRequest> getDeliveryRequests() {
        return this.DeliveryRequestService.findAllDeliveryRequests();
    }

    @GetMapping({"/deliveryrequest/{id}"})
    public DeliveryRequest getDeliveryRequestById(@PathVariable("id") final Long id) {
        return (DeliveryRequest)this.DeliveryRequestService.findDeliveryRequestById(id).orElse(null);
    }
}
