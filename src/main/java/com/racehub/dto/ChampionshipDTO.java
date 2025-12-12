package com.racehub.dto;

import com.racehub.model.Championship;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChampionshipDTO {
    private Long id;
    private String name;
    private Integer season;
    private String status;
    private String description;
    private Integer maxPilots;
    private String pointsSystem;

    // Constructor da Entity
    public ChampionshipDTO(Championship championship) {
        this.id = championship.getId();
        this.name = championship.getName();
        this.season = championship.getSeason();
        this.status = championship.getStatus();
        this.description = championship.getDescription();
        this.maxPilots = championship.getMaxPilots();
        this.pointsSystem = championship.getPointsSystem();
    }
}
