package com.ryanair.finterconnection.controller;

import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.dto.InterconnectionDTO;
import com.ryanair.finterconnection.dto.LegDTO;
import com.ryanair.finterconnection.service.IFlightConnectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.AbstractMap;
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
        Leg flightRequirements = new Leg(departure, arrival, departureDateTime, arrivalDateTime);
        List<Leg> directLegs = flightConnectionService.getDirectConnections(flightRequirements);
        logger.log(Level.INFO, "Direct Flights obtained");
        List<InterconnectionDTO> interconnections = new ArrayList<>(directLegs.stream()
                .map(dL -> new InterconnectionDTO(0, List.of(mapLegToLegDTO(dL))))
                .toList());
        List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> oneStopLegs =
                flightConnectionService.getOneStepConnections(flightRequirements);
        logger.log(Level.INFO, "One Stop Flights obtained");
        interconnections.addAll(oneStopLegs.stream()
                .map(twoL -> {
                    Leg firstLeg = twoL.getKey();
                    Leg secondLeg = twoL.getValue();
                    LegDTO firstLegDTO = mapLegToLegDTO(firstLeg);
                    LegDTO secondLegDTO = mapLegToLegDTO(secondLeg);
                    return new InterconnectionDTO(1, List.of(firstLegDTO, secondLegDTO));
                }).toList());

        return interconnections;
    }

    private LegDTO mapLegToLegDTO(Leg leg) {
        return new LegDTO(
                leg.departureAirport(), leg.arrivalAirport(),
                leg.departureDateTime().toString(), leg.arrivalDateTime().toString());
    }
}
