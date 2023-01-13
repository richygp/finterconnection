package com.ryanair.finterconnection.apiclients.ryanairscheduleclient;

import com.ryanair.finterconnection.dto.ScheduleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RyanairScheduleApiClientImpl implements IRyanairScheduleApiClient {
    @Value("${ryanair.services.url:https://services-api.ryanair.com/}")
    private String ryanairServicesUrl;
    @Value("${ryanair.schedules.path:timtbl/3/schedules}")
    private String path;

    @Override
    public ScheduleDTO getScheduleForYearAndMonth(String departure, String arrival, String year, String month) {
        WebClient client = WebClient.builder()
                .baseUrl(this.ryanairServicesUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        var completePath = this.path.concat("/").concat(departure)
                .concat("/").concat(arrival)
                .concat("/years/").concat(year)
                .concat("/months/").concat(month);

        return client.get()
                .uri(completePath)
                .retrieve()
                .bodyToMono(ScheduleDTO.class)
                .block();
    }
}
