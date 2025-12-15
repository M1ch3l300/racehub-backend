package com.racehub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "championships")
@Data
@EqualsAndHashCode(exclude = {"pilots", "races"})
@NoArgsConstructor
@AllArgsConstructor
public class Championship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer season;

    private String status = "UPCOMING";

    @Column(length = 1000)
    private String description;

    @Column(name = "max_pilots")
    private Integer maxPilots = 20;

    @Column(name = "points_system")
    private String pointsSystem = "25,18,15,12,10,8,6,4,2,1";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "championship_pilots",
            joinColumns = @JoinColumn(name = "championship_id"),
            inverseJoinColumns = @JoinColumn(name = "pilot_id")
    )
    @JsonIgnore
    private Set<Pilot> pilots = new HashSet<>();

    @OneToMany(mappedBy = "championship", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Race> races;

    // 🔥 METODO MAGICO - Conta i piloti automaticamente!
    @Transient
    public Integer getEnrolledPilotsCount() {
        return pilots != null ? pilots.size() : 0;
    }
}
