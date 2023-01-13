package com.ryanair.finterconnection.apiclients.ryanairscheduleclient;

import com.ryanair.finterconnection.dto.ScheduleDTO;

public interface IRyanairScheduleApiClient {
    ScheduleDTO getScheduleForYearAndMonth(String departure, String arrival, String year, String month);
}
