package com.hexplosif.OptimodBackEnd;

import com.hexplosif.OptimodBackEnd.model.Courier;
import com.hexplosif.OptimodBackEnd.repository.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OptimodBackEndApplication implements CommandLineRunner {

    @Autowired
    private CourierRepository courierRepository;

    public static void main(String[] args) {
        SpringApplication.run(OptimodBackEndApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        Init Courier data
         */
        Courier courier = new Courier();
        courier.setName("Courier 1");
        courierRepository.save(courier);
    }

}
