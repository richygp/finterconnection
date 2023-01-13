package com.ryanair.finterconnection.controller;

import com.ryanair.finterconnection.dto.RouteDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import com.ryanair.finterconnection.apiclients.ryanairroutesclient.IRyanairRoutesApiClient;
import com.ryanair.finterconnection.apiclients.ryanairscheduleclient.IRyanairScheduleApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FinterconnectionController {
    private final IRyanairRoutesApiClient ryanairRoutesApiClient;
    private final IRyanairScheduleApiClient ryanairScheduleApiClient;

    public FinterconnectionController(
            IRyanairRoutesApiClient ryanairRoutesApiClient, IRyanairScheduleApiClient ryanairScheduleApiClient) {
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
}
