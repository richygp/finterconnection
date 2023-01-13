package com.ryanair.finterconnection.dto;

import java.time.LocalTime;

public record FlightDTO(String carrierCode, String number, LocalTime departureTime, LocalTime arrivalTime) {
}
