package com.ryanair.finterconnection.apiclients.ryanairroutesclient;

import com.ryanair.finterconnection.dto.RouteDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8787})
class RyanairRoutesApiClientImplTest {
    private final IRyanairRoutesApiClient ryanairRoutesApiClient;
    private final ClientAndServer client;

    RyanairRoutesApiClientImplTest(ClientAndServer client) {
        this.client = client;
        this.ryanairRoutesApiClient =
                new RyanairRoutesApiClientImpl("http://localhost:8787", "locate/3/routes", 40);
    }

    @Test
    void whenRequestForRoutesAndOkay() {
        // Given
        this.client.when(HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/locate/3/routes"),
                        Times.exactly(1))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("[\n" +
                                "    {\n" +
                                "        \"airportFrom\": \"AAL\",\n" +
                                "        \"airportTo\": \"KUN\",\n" +
                                "        \"connectingAirport\": null,\n" +
                                "        \"newRoute\": false,\n" +
                                "        \"seasonalRoute\": false,\n" +
                                "        \"operator\": \"RYANAIR\",\n" +
                                "        \"carrierCode\": \"FR\",\n" +
                                "        \"group\": \"GENERIC\",\n" +
                                "        \"similarArrivalAirportCodes\": [],\n" +
                                "        \"tags\": []\n" +
                                "    },\n" +
                                "    {\n" +
                                "        \"airportFrom\": \"AAL\",\n" +
                                "        \"airportTo\": \"STN\",\n" +
                                "        \"connectingAirport\": null,\n" +
                                "        \"newRoute\": false,\n" +
                                "        \"seasonalRoute\": false,\n" +
                                "        \"operator\": \"RYANAIR\",\n" +
                                "        \"carrierCode\": \"FR\",\n" +
                                "        \"group\": \"CITY\",\n" +
                                "        \"similarArrivalAirportCodes\": [],\n" +
                                "        \"tags\": []\n" +
                                "    },\n" +
                                "    {\n" +
                                "        \"airportFrom\": \"AAR\",\n" +
                                "        \"airportTo\": \"AGP\",\n" +
                                "        \"connectingAirport\": null,\n" +
                                "        \"newRoute\": false,\n" +
                                "        \"seasonalRoute\": false,\n" +
                                "        \"operator\": \"RYANAIR\",\n" +
                                "        \"carrierCode\": \"FR\",\n" +
                                "        \"group\": \"LEISURE\",\n" +
                                "        \"similarArrivalAirportCodes\": [],\n" +
                                "        \"tags\": []\n" +
                                "    }]"));
        // When
        List<RouteDTO> routeDTOList = List.of(
                new RouteDTO("AAL", "KUN", null, false, false, "RYANAIR", "GENERIC"),
                new RouteDTO("AAL", "STN", null, false, false, "RYANAIR", "CITY"),
                new RouteDTO("AAR", "AGP", null, false, false, "RYANAIR", "LEISURE")
                );

        // Then
        assertEquals(routeDTOList, ryanairRoutesApiClient.getAvailableRoutes());
    }
}