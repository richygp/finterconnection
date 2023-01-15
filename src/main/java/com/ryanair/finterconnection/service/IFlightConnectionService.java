package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.domain.Leg;

import java.util.AbstractMap;
import java.util.List;

public interface IFlightConnectionService {
    List<Leg> getDirectConnections(Leg flightRequirements);
    List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> getOneStepConnections(Leg flightRequirements);
}
