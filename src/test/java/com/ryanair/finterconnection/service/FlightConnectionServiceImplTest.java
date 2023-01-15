package com.ryanair.finterconnection.service;

import com.ryanair.finterconnection.apiclients.ryanairroutesclient.IRyanairRoutesApiClient;
import com.ryanair.finterconnection.apiclients.ryanairscheduleclient.IRyanairScheduleApiClient;
import com.ryanair.finterconnection.domain.Leg;
import com.ryanair.finterconnection.dto.DayDTO;
import com.ryanair.finterconnection.dto.FlightDTO;
import com.ryanair.finterconnection.dto.RouteDTO;
import com.ryanair.finterconnection.dto.ScheduleDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FlightConnectionServiceImplTest {
    @Mock
    private IRyanairRoutesApiClient ryanairRoutesApiClient;
    @Mock
    private IRyanairScheduleApiClient ryanairScheduleApiClient;
    @InjectMocks
    private FlightConnectionServiceImpl flightConnectionService;

    @Test
    void whenGetDirectFlightsAndOkay() {
        // Given
        ScheduleDTO scheduleDTO = new ScheduleDTO(
                6, List.of(
                new DayDTO(1, List.of(
                        new FlightDTO(
                                "FR", "9648", LocalTime.of(21, 35), LocalTime.of(23, 50))
                ))
                , new DayDTO(15, List.of(
                        new FlightDTO(
                                "FR", "9648", LocalTime.of(21, 35), LocalTime.of(23, 50))
                ))
        ));
        given(ryanairScheduleApiClient.getScheduleForYearAndMonth(anyString(), anyString(), any(LocalDateTime.class)))
                .willReturn(scheduleDTO);

        // When
        List<Leg> legs = flightConnectionService.getDirectConnections(
                new Leg("LPL", "WRO",
                        LocalDateTime.of(2023, Month.JUNE, 14, 12, 15),
                        LocalDateTime.of(2023, Month.JUNE, 17, 12, 15)
                )
        );

        // Then
        List<Leg> expectedLegs = List.of(new Leg("LPL", "WRO",
                LocalDateTime.of(2023, Month.JUNE, 15, 21, 35),
                LocalDateTime.of(2023, Month.JUNE, 15, 23, 50)));
        assertEquals(expectedLegs, legs);
    }

    @Test
    void whenGetOneStopFlightsAndOkay() {
        // Given
        given(ryanairRoutesApiClient.getAvailableRoutes()).willReturn(List.of(
                new RouteDTO("DUB", "STN", null, false, false, "RYANAIR", "GENERIC"),
                new RouteDTO("STN", "WRO", null, false, false, "RYANAIR", "GENERIC"),
                new RouteDTO("DUB", "WRO", null, false, false, "RYANAIR", "GENERIC")
        ));
        ScheduleDTO scheduleDTO = new ScheduleDTO(
                6, List.of(
                new DayDTO(1, List.of(
                        new FlightDTO(
                                "FR", "9648", LocalTime.of(21, 35), LocalTime.of(23, 50))
                ))
                , new DayDTO(15, List.of(
                        new FlightDTO(
                                "FR", "9648", LocalTime.of(21, 35), LocalTime.of(23, 50))
                ))
        ));
        given(ryanairScheduleApiClient.getScheduleForYearAndMonth(anyString(), anyString(), any(LocalDateTime.class)))
                .willReturn(scheduleDTO);

        // When
        List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> legs = flightConnectionService.getOneStepConnections(
                new Leg("DUB", "WRO",
                        LocalDateTime.of(2023, Month.JUNE, 1, 0, 0),
                        LocalDateTime.of(2023, Month.JUNE, 30, 23, 59)
                )
        );

        // Then
        List<AbstractMap.SimpleImmutableEntry<Leg, Leg>> expected = new ArrayList<>();
        expected.add(new AbstractMap.SimpleImmutableEntry<>(
                new Leg("DUB", "STN",
                        LocalDateTime.of(2023, Month.JUNE, 1, 21, 35),
                        LocalDateTime.of(2023, Month.JUNE, 1, 23, 50)),
                new Leg("STN", "WRO",
                        LocalDateTime.of(2023, Month.JUNE, 15, 21, 35),
                        LocalDateTime.of(2023, Month.JUNE, 15, 23, 50))
                )
        );
        assertEquals(expected, legs);
    }

}
