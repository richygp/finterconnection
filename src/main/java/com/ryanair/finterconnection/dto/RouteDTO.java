package com.ryanair.finterconnection.dto;

public record RouteDTO(
        String airportFrom, String airportTo, String connectingAirport,
        boolean newRoute, boolean seasonalRoute,
        String operator, String group) {

}
