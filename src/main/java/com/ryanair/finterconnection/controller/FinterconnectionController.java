package com.ryanair.finterconnection.controller;

import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.dto.InterconnectionDTO;
import com.ryanair.finterconnection.dto.LegDTO;
import com.ryanair.finterconnection.service.IFlightConnectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FinterconnectionController {
    private static final Logger logger = Logger.getLogger(FinterconnectionController.class.getName());
    private final IFlightConnectionService flightConnectionService;

    public FinterconnectionController(IFlightConnectionService flightConnectionService) {
        logger.log(Level.INFO, "Starting up the Ryanair Flight Interconnection Controller...");
        this.flightConnectionService = flightConnectionService;
    }

    @GetMapping("/interconnections")
    public List<InterconnectionDTO> getInterconnections(@RequestParam String departure, @RequestParam String arrival,
                                                        @RequestParam LocalDateTime departureDateTime,
                                                        @RequestParam LocalDateTime arrivalDateTime) {
        List<Leg> directLegs = flightConnectionService.getDirectConnections(
                departure, arrival, departureDateTime, arrivalDateTime);
        directLegs.forEach(l ->
                logger.log(Level.INFO, "Direct connection found :{0} departing at {1} --> {2} arriving at {3}",
                new Object[]{l.departureAirport(), l.departureDateTime(), l.arrivalAirport(), l.arrivalDateTime()}));
        List<InterconnectionDTO> interconnections = new ArrayList<>();
        interconnections.add(new InterconnectionDTO(0, directLegs.stream()
                .map(dL -> new LegDTO(
                        dL.departureAirport(),
                        dL.arrivalAirport(),
                        dL.departureDateTime().toString(),
                        dL.arrivalDateTime().toString())
                ).toList())
        );

        return interconnections;
    }
}
