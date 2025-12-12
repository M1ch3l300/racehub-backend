package com.racehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PilotStandingDTO {
    private PilotDTO pilot;
    private Integer points;
}
