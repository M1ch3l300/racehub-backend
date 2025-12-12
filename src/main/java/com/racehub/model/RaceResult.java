package com.racehub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "race_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "race_id", nullable = false)
    @JsonIgnoreProperties({"results", "championship"})  // ← AGGIUNGI QUESTA!
    private Race race;

    @ManyToOne
    @JoinColumn(name = "pilot_id", nullable = false)
    @JsonIgnoreProperties({"championships"})  // ← AGGIUNGI QUESTA!
    private Pilot pilot;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(name = "fastest_lap")
    private Boolean fastestLap = false;

    private String status = "FINISHED"; // FINISHED, DNF, DSQ

    private String notes;
}
