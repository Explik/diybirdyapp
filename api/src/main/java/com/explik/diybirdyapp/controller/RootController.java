package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.service.DataInitializerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @Autowired
    private DataInitializerService dataInitializerService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/reset-graph")
    public void resetGraph() {
        dataInitializerService.resetInitialData();
    }
}
