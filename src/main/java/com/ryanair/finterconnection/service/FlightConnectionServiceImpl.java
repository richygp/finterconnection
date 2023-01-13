package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.apiclients.ryanairroutesclient.IRyanairRoutesApiClient;
import com.ryanair.finterconnection.apiclients.ryanairscheduleclient.IRyanairScheduleApiClient;
import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.domain.Route;
import com.ryanair.finterconnection.dto.DayDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

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
    public List<Leg> getDirectConnections(String departure, String arrival,
                                          LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        ScheduleDTO schedule = scheduleApiClient.getScheduleForYearAndMonth(departure, arrival, departureDateTime);
        List<DayDTO> days = schedule.days().stream()
                .filter(d -> d.day() >= departureDateTime.getDayOfMonth() && d.day() <= arrivalDateTime.getDayOfMonth())
                .toList();

        return days.stream()
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
    public List<Route> getIntermediateConnections(String origin, String destination) {
        // TODO: get one stop connections and filter based on connection time > 2h and arrival date times
        return null;
    }
}
