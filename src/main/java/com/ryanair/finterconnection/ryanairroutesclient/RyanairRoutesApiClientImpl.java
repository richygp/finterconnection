package com.ryanair.finterconnection.ryanairroutesclient;

import com.ryanair.finterconnection.dto.RouteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class RyanairRoutesApiClientImpl implements IRyanairRoutesApiClient {
    @Value("${ryanair.services.url:https://services-api.ryanair.com/}")
    private String ryanairServicesUrl;
    @Value("${ryanair.routes.path:locate/3/routes}")
    private String path;

    @Override
    public List<RouteDTO> getAvailableRoutes() {
        WebClient client = WebClient.builder()
                .baseUrl(this.ryanairServicesUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.get()
                .uri(this.path)
                .retrieve()
                .bodyToFlux(RouteDTO.class)
                .collectList()
                .block();
    }
}
