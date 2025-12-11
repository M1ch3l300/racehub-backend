package com.racehub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "championships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Championship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Championship name is required")
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Season is required")
    @Min(value = 2020)
    @Max(value = 2030)
    @Column(nullable = false)
    private Integer season;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status; // UPCOMING, ACTIVE, COMPLETED

    @Size(max = 500)
    private String description;

    @Column(name = "max_pilots")
    private Integer maxPilots = 20; // Default F1 grid

    @Column(name = "points_system")
    private String pointsSystem = "25,18,15,12,10,8,6,4,2,1"; // F1 standard

    @ManyToMany
    @JoinTable(
            name = "championship_pilots",
            joinColumns = @JoinColumn(name = "championship_id"),
            inverseJoinColumns = @JoinColumn(name = "pilot_id")
    )
    private Set<Pilot> pilots = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
