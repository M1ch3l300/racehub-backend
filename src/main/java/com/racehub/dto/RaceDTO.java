package com.racehub.dto;

import com.racehub.model.Race;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceDTO {
    private Long id;
    private Long championshipId;
    private String championshipName;
    private String circuit;
    private Integer round;
    private LocalDateTime raceDate;
    private String status;
    private String notes;

    // Constructor da Entity
    public RaceDTO(Race race) {
        this.id = race.getId();
        this.championshipId = race.getChampionship().getId();
        this.championshipName = race.getChampionship().getName();
        this.circuit = race.getCircuit();
        this.round = race.getRound();
        this.raceDate = race.getRaceDate();
        this.status = race.getStatus();
        this.notes = race.getNotes();
    }
}
