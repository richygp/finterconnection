package com.ryanair.finterconnection.apiclients.ryanairroutesclient;

import com.ryanair.finterconnection.dto.RouteDTO;

import java.util.List;

public interface IRyanairRoutesApiClient {
    List<RouteDTO> getAvailableRoutes();
}
