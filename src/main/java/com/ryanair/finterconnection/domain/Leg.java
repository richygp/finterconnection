package com.ryanair.finterconnection.domain;

import java.time.LocalDateTime;

public record Leg(String departureAirport, String arrivalAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
}
