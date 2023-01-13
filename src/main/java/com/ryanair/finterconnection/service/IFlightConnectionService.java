package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.domain.Route;

import java.time.LocalDateTime;
import java.util.List;

public interface IFlightConnectionService {
    List<Leg> getDirectConnections(String origin, String destination,
                                   LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
    List<Route> getIntermediateConnections(String origin, String destination);
}
