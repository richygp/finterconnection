package com.ryanair.finterconnection.controller;

import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.service.IFlightConnectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class FinterconnectionControllerTest {
    @Mock
    private IFlightConnectionService flightConnectionService;
    @InjectMocks
    private FinterconnectionController finterconnectionController;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        this.mvc = MockMvcBuilders
                .standaloneSetup(finterconnectionController)
                .build();
    }

    @Test
    void whenRequestingFlightAndEmpty() throws Exception {
        // Given
        given(flightConnectionService.getDirectConnections(any(Leg.class)))
                .willReturn(Collections.emptyList());
        given(flightConnectionService.getOneStepConnections(any(Leg.class)))
                .willReturn(Collections.emptyList());

        // When
        MockHttpServletResponse response = mvc.perform(
                get("/interconnections")
                        .param("departure", "DUB")
                        .param("arrival", "WRO")
                        .param("departureDateTime", "2023-06-01T07:00")
                        .param("arrivalDateTime", "2023-06-02T21:00"))
                .andReturn()
                .getResponse();

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals("[]", response.getContentAsString());
    }

    @Test
    void whenRequestingFlightAndOkay() throws Exception {
        // Given
        given(flightConnectionService.getDirectConnections(any(Leg.class)))
                .willReturn(List.of(new Leg("DUB", "WRO",
                        LocalDateTime.of(2023, Month.JUNE,2,18,10),
                        LocalDateTime.of(2023, Month.JUNE,2,21,45))));
        given(flightConnectionService.getOneStepConnections(any(Leg.class)))
                .willReturn(List.of(
                        new AbstractMap.SimpleImmutableEntry<>(
                                new Leg("DUB", "STN",
                                        LocalDateTime.of(2023, Month.JUNE, 1, 8, 25),
                                        LocalDateTime.of(2023, Month.JUNE, 1, 9, 45)),
                                new Leg("STN", "WRO",
                                        LocalDateTime.of(2023, Month.JUNE, 2, 12, 15),
                                        LocalDateTime.of(2023, Month.JUNE, 2, 15, 15))),
                        new AbstractMap.SimpleImmutableEntry<>(
                                new Leg("DUB", "STN",
                                        LocalDateTime.of(2023, Month.JUNE, 1, 8, 25),
                                        LocalDateTime.of(2023, Month.JUNE, 1, 9, 45)),
                                new Leg("STN", "WRO",
                                        LocalDateTime.of(2023, Month.JUNE, 2, 12, 15),
                                        LocalDateTime.of(2023, Month.JUNE, 2, 15, 15)))
                ));

        // When
        MockHttpServletResponse response = mvc.perform(
                        get("/interconnections")
                                .param("departure", "DUB")
                                .param("arrival", "WRO")
                                .param("departureDateTime", "2023-06-01T07:00")
                                .param("arrivalDateTime", "2023-06-02T21:00"))
                .andReturn()
                .getResponse();

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals("[{\"stops\":0,\"legs\":[{\"departureAirport\":\"DUB\",\"arrivalAirport\":\"WRO\",\"departureDataTime\":\"2023-06-02T18:10\",\"arrivalDateTime\":\"2023-06-02T21:45\"}]},{\"stops\":1,\"legs\":[{\"departureAirport\":\"DUB\",\"arrivalAirport\":\"STN\",\"departureDataTime\":\"2023-06-01T08:25\",\"arrivalDateTime\":\"2023-06-01T09:45\"},{\"departureAirport\":\"STN\",\"arrivalAirport\":\"WRO\",\"departureDataTime\":\"2023-06-02T12:15\",\"arrivalDateTime\":\"2023-06-02T15:15\"}]},{\"stops\":1,\"legs\":[{\"departureAirport\":\"DUB\",\"arrivalAirport\":\"STN\",\"departureDataTime\":\"2023-06-01T08:25\",\"arrivalDateTime\":\"2023-06-01T09:45\"},{\"departureAirport\":\"STN\",\"arrivalAirport\":\"WRO\",\"departureDataTime\":\"2023-06-02T12:15\",\"arrivalDateTime\":\"2023-06-02T15:15\"}]}]", response.getContentAsString());
    }
}