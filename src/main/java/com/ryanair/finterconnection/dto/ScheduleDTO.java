package com.ryanair.finterconnection.dto;

import java.util.List;

public record ScheduleDTO(int month, List<DayDTO> days) {
}
