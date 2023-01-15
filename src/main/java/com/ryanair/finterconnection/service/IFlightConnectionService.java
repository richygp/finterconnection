package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.domain.Leg;

import java.util.List;

public interface IFlightConnectionService {
    List<Leg> getDirectConnections(Leg flightRequirements);
    List<Leg> getOneStepConnections(Leg flightRequirements);
}
