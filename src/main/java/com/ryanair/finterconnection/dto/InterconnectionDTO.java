package com.ryanair.finterconnection.dto;

import java.util.List;

public record InterconnectionDTO(int stops, List<LegDTO> legs) {
}
