package com.ryanair.finterconnection.apiclients.ryanairscheduleclient;

import com.ryanair.finterconnection.dto.ScheduleDTO;

import java.time.LocalDateTime;

public interface IRyanairScheduleApiClient {
    ScheduleDTO getScheduleForYearAndMonth(String departure, String arrival, LocalDateTime departureDateTime);
}
