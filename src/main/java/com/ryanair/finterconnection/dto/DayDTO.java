package com.ryanair.finterconnection.dto;

import java.util.List;

public record DayDTO(int day, List<FlightDTO> flights) {
}
