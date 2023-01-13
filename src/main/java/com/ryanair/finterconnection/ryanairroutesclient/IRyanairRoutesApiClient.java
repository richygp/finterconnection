package com.ryanair.finterconnection.ryanairroutesclient;

import com.ryanair.finterconnection.dto.RouteDTO;

import java.util.List;

public interface IRyanairRoutesApiClient {
    List<RouteDTO> getAvailableRoutes();
}
