package com.ryanair.finterconnection.apiclients.ryanairscheduleclient;

import com.ryanair.finterconnection.dto.DayDTO;
import com.ryanair.finterconnection.dto.FlightDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8787})
class RyanairScheduleApiClientImplTest {
    private final IRyanairScheduleApiClient ryanairScheduleApiClient;
    private final ClientAndServer client;

    RyanairScheduleApiClientImplTest(ClientAndServer client) {
        this.client = client;
        this.ryanairScheduleApiClient =
                new RyanairScheduleApiClientImpl("http://localhost:8787", "timtbl/3/schedules", 40);
    }

    @Test
    void whenRequestForRoutesAndOkay() {
        // Given
        this.client.when(HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/timtbl/3/schedules/LPL/WRO/years/2023/months/6"),
                        Times.exactly(1))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("{\n" +
                                "    \"month\": 6,\n" +
                                "    \"days\": [\n" +
                                "        {\n" +
                                "            \"day\": 1,\n" +
                                "            \"flights\": [\n" +
                                "                {\n" +
                                "                    \"carrierCode\": \"FR\",\n" +
                                "                    \"number\": \"9648\",\n" +
                                "                    \"departureTime\": \"21:35\",\n" +
                                "                    \"arrivalTime\": \"00:50\"\n" +
                                "                }\n" +
                                "            ]\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"day\": 15,\n" +
                                "            \"flights\": [\n" +
                                "                {\n" +
                                "                    \"carrierCode\": \"FR\",\n" +
                                "                    \"number\": \"9648\",\n" +
                                "                    \"departureTime\": \"21:35\",\n" +
                                "                    \"arrivalTime\": \"00:50\"\n" +
                                "                }\n" +
                                "            ]\n" +
                                "        }\n" +
                                "    ]\n" +
                                "}"));
        // When
        ScheduleDTO scheduleDTO = new ScheduleDTO(
                6, List.of(
                new DayDTO(1, List.of(
                        new FlightDTO(
                                "FR", "9648", LocalTime.of(21, 35), LocalTime.of(0, 50))
                ))
                , new DayDTO(15, List.of(
                        new FlightDTO(
                                "FR", "9648", LocalTime.of(21, 35), LocalTime.of(0, 50))))));

        // Then
        assertEquals(scheduleDTO, ryanairScheduleApiClient.getScheduleForYearAndMonth(
                "LPL" ,"WRO",
                LocalDateTime.of(2023, Month.JUNE, 2, 12, 15)));
    }
}