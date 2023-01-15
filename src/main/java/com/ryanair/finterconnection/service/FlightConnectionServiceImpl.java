package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.apiclients.ryanairroutesclient.IRyanairRoutesApiClient;
import com.ryanair.finterconnection.apiclients.ryanairscheduleclient.IRyanairScheduleApiClient;
import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.domain.Route;
import com.ryanair.finterconnection.dto.RouteDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class FlightConnectionServiceImpl implements IFlightConnectionService {
    private static final Logger logger = Logger.getLogger(FlightConnectionServiceImpl.class.getName());
    public static final long MIN_INTERCONNECTION_WAITING_HOURS = 2L;
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
        logger.log(Level.INFO,
                "Filtering all possible flights for departure from: {0} {1} and arrival at: {2} {3}",
                new Object[]{departure, departureDateTime, arrival, arrivalDateTime});

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
    public List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> getOneStepConnections(Leg flightRequirements) {
        List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> feasibleFlights = new ArrayList<>();
        List<Callable<List<AbstractMap.SimpleImmutableEntry<Leg, Leg>>>> routeFlights = new ArrayList<>();
        populateRouteFunctions(routeFlights, flightRequirements);
        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            executor.invokeAll(routeFlights).stream()
                    .map(future -> {
                        List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> lFlights = new ArrayList<>();
                        try {
                            lFlights = future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            logger.log(Level.SEVERE, "Error while trying to get Futures in thread execution");
                            Thread.currentThread().interrupt();
                        }
                        return lFlights;
                    })
                    .forEach(feasibleFlights::addAll);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted Exception while multithreading execution");
            Thread.currentThread().interrupt();
        }

        return feasibleFlights;
    }

    private void populateRouteFunctions(List<Callable<List<AbstractMap.SimpleImmutableEntry<Leg, Leg>>>> routeFlights,
                                        Leg flightRequirements) {
        String departure = flightRequirements.departureAirport();
        String arrival = flightRequirements.arrivalAirport();
        LocalDateTime departureDateTime = flightRequirements.departureDateTime();
        LocalDateTime arrivalDateTime = flightRequirements.arrivalDateTime();
        for (Route route: getOneStopAvailableRoutes(departure, arrival)) {
            routeFlights.add(() -> {
                List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> rFlights = new ArrayList<>();
                Leg firstLegRequirements = new Leg(
                        route.origin(), route.intermediateStep(),
                        departureDateTime, arrivalDateTime);
                List<Leg> firstLegList = getDirectConnections(firstLegRequirements);
                for (Leg firstLeg: firstLegList) {
                    Leg secondLegRequirements = new Leg(
                            route.intermediateStep(), route.destination(),
                            firstLeg.arrivalDateTime().plusHours(MIN_INTERCONNECTION_WAITING_HOURS), arrivalDateTime);
                    rFlights.addAll(getDirectConnections(secondLegRequirements).stream()
                            .map(secondLeg -> new AbstractMap.SimpleImmutableEntry<>(firstLeg, secondLeg))
                            .toList());
                }
                return rFlights;
            });
        }
    }

    private List<Route> getOneStopAvailableRoutes(String origin, String destination) {
        List<RouteDTO> routes = routesApiClient.getAvailableRoutes();
        logger.log(Level.INFO,
                "Reckoning one step available interconnections to departure from: {0} and arrival at: {1}",
                new Object[]{origin, destination});
        Set<String> originRoutes = routes.stream()
                .filter(r -> (r.airportFrom().equals(origin) && !r.airportTo().equals(destination)))
                .map(RouteDTO::airportTo)
                .collect(Collectors.toSet());
        Set<String> destinationRoutes = routes.stream()
                .filter(r -> (r.airportTo().equals(destination) && !r.airportFrom().equals(origin)))
                .map(RouteDTO::airportFrom)
                .collect(Collectors.toSet());
        // Intersection of both intermediate destinations
        originRoutes.retainAll(destinationRoutes);
        logger.log(Level.INFO, "Available intermediate stops: {0}", originRoutes);

        return originRoutes.stream()
                .map(i -> new Route(origin, i, destination))
                .toList();
    }
}
