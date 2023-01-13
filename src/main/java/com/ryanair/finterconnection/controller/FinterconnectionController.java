package com.ryanair.finterconnection.controller;

import com.ryanair.finterconnection.dto.InterconnectionDTO;
import com.ryanair.finterconnection.dto.RouteDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import com.ryanair.finterconnection.apiclients.ryanairroutesclient.IRyanairRoutesApiClient;
import com.ryanair.finterconnection.apiclients.ryanairscheduleclient.IRyanairScheduleApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FinterconnectionController {
    private static final Logger logger = Logger.getLogger(FinterconnectionController.class.getName());
    private final IRyanairRoutesApiClient ryanairRoutesApiClient;
    private final IRyanairScheduleApiClient ryanairScheduleApiClient;

    public FinterconnectionController(
            IRyanairRoutesApiClient ryanairRoutesApiClient, IRyanairScheduleApiClient ryanairScheduleApiClient) {
        logger.log(Level.INFO, "Starting up the Ryanair Flight Interconnection Controller...");
        this.ryanairRoutesApiClient = ryanairRoutesApiClient;
        this.ryanairScheduleApiClient = ryanairScheduleApiClient;
    }

    @GetMapping("/routes")
    public List<RouteDTO> getRoutes() {
        return ryanairRoutesApiClient.getAvailableRoutes();
    }

    @GetMapping("/schedules")
    public ScheduleDTO getSchedule() {
        return ryanairScheduleApiClient.getScheduleForYearAndMonth("DUB", "WRO", "2023", "6");
    }

    @GetMapping("/interconnections")
    public List<InterconnectionDTO> getInterconnections(@RequestParam Map<String,String> paramsAsMap) {
        logger.log(Level.INFO, "Parameters are {0}", paramsAsMap.entrySet());
        return new ArrayList<>();
    }
}
