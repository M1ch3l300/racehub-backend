package com.racehub.dto;

import com.racehub.model.RaceResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceResultDTO {
    private Long id;
    private PilotDTO pilot;
    private Integer position;
    private Integer points;
    private Boolean fastestLap;
    private String status;
    private String notes;

    // Constructor da Entity
    public RaceResultDTO(RaceResult result) {
        this.id = result.getId();
        this.pilot = new PilotDTO(result.getPilot());
        this.position = result.getPosition();
        this.points = result.getPoints();
        this.fastestLap = result.getFastestLap();
        this.status = result.getStatus();
        this.notes = result.getNotes();
    }
}
