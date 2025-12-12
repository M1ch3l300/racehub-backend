package com.racehub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "races")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "championship_id", nullable = false)
    @JsonIgnoreProperties({"races", "pilots"})  // ← AGGIUNGI!
    private Championship championship;

    @Column(nullable = false)
    private String circuit;

    @Column(nullable = false)
    private Integer round;

    @Column(name = "race_date")
    private LocalDateTime raceDate;

    private String status = "SCHEDULED"; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    private String notes;

    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"race"})  // ← AGGIUNGI!
    private List<RaceResult> results;
}
