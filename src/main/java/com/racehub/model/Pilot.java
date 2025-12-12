package com.racehub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pilots")
@Data
@EqualsAndHashCode(exclude = {"championships"})  // ← AGGIUNGI QUESTA!
@NoArgsConstructor
@AllArgsConstructor
public class Pilot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String team;

    @Column(name = "racing_number")
    private Integer racingNumber;

    private String nationality;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(mappedBy = "pilots")
    @JsonIgnore
    private Set<Championship> championships = new HashSet<>();
}
