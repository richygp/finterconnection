package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.apiclients.ryanairroutesclient.IRyanairRoutesApiClient;
import com.ryanair.finterconnection.apiclients.ryanairscheduleclient.IRyanairScheduleApiClient;
import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.domain.Route;
import com.ryanair.finterconnection.dto.RouteDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class FlightConnectionServiceImpl implements IFlightConnectionService {
    private static final Logger logger = Logger.getLogger(FlightConnectionServiceImpl.class.getName());
    private final IRyanairRoutesApiClient routesApiClient;
    private final IRyanairScheduleApiClient scheduleApiClient;

    public FlightConnectionServiceImpl(
            IRyanairRoutesApiClient routesApiClient, IRyanairScheduleApiClient scheduleApiClient) {
        this.routesApiClient = routesApiClient;
        this.scheduleApiClient = scheduleApiClient;
    }
    @Override
    public List<Leg> getDirectConnections(Leg flightRequirements) {
        String departure = flightRequirements.departureAirport();
        String arrival = flightRequirements.arrivalAirport();
        LocalDateTime departureDateTime = flightRequirements.departureDateTime();
        LocalDateTime arrivalDateTime = flightRequirements.arrivalDateTime();
        ScheduleDTO schedule = scheduleApiClient.getScheduleForYearAndMonth(departure, arrival, departureDateTime);
        logger.log(Level.INFO, "Filtering all possible flights for a given month based on flight requirements...");

        return schedule.days().stream()
                .filter(d -> d.day() >= departureDateTime.getDayOfMonth() && d.day() <= arrivalDateTime.getDayOfMonth())
                .flatMap(d -> d.flights().stream()
                        .map((f -> new Leg(
                                        departure, arrival,
                                        LocalDateTime.of(
                                                departureDateTime.getYear(),
                                                departureDateTime.getMonth(),
                                                d.day(),
                                                f.departureTime().getHour(),
                                                f.departureTime().getMinute()),
                                        LocalDateTime.of(
                                                arrivalDateTime.getYear(),
                                                arrivalDateTime.getMonth(),
                                                d.day(),
                                                f.arrivalTime().getHour(),
                                                f.arrivalTime().getMinute()))
                                )
                        ).filter(l ->
                                l.departureDateTime().isAfter(departureDateTime) &&
                                        l.arrivalDateTime().isBefore(arrivalDateTime)
                        )
                ).toList();
    }

    @Override
    public List<Leg> getOneStepConnections(Leg flightRequirements) {
        String departure = flightRequirements.departureAirport();
        String arrival = flightRequirements.arrivalAirport();
        getOneStopAvailableRoutes(departure, arrival);

        // TODO: get one stop connections and filter based on connection time > 2h and arrival date times
        return null;
    }

    private List<Route> getOneStopAvailableRoutes(String origin, String destination) {
        List<RouteDTO> routes = routesApiClient.getAvailableRoutes();
        logger.log(Level.INFO,
                "Reducing all possible one step interconnections to those which match with departure and arrival...");
        Set<String> originRoutes = routes.stream()
                .filter(r -> (r.airportFrom().equals(origin) && !r.airportTo().equals(destination)))
                .map(RouteDTO::airportTo)
                .collect(Collectors.toSet());
        Set<String> destinationRoutes = routes.stream()
                .filter(r -> (r.airportTo().equals(destination) && !r.airportFrom().equals(origin)))
                .map(RouteDTO::airportFrom)
                .collect(Collectors.toSet());
        originRoutes.retainAll(destinationRoutes);

        return originRoutes.stream()
                .map(i -> new Route(origin, i, destination))
                .toList();
    }
}
