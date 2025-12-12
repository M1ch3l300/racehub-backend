package com.racehub.dto;

import com.racehub.model.Pilot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PilotDTO {
    private Long id;
    private String name;
    private String email;
    private String team;
    private Integer racingNumber;
    private String nationality;

    // Constructor da Entity
    public PilotDTO(Pilot pilot) {
        this.id = pilot.getId();
        this.name = pilot.getName();
        this.email = pilot.getEmail();
        this.team = pilot.getTeam();
        this.racingNumber = pilot.getRacingNumber();
        this.nationality = pilot.getNationality();
    }
}
