package com.racehub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "championship_id")
    private Championship championship;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private Race race;

    @NotBlank(message = "Type is required")
    @Column(nullable = false)
    private String type; // NEWS, PENALTY, DECISION, UPDATE, GENERAL

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Boolean pinned = false;

    @Column(nullable = false)
    private Boolean published = true;

    @ManyToOne
    @JoinColumn(name = "pilot_id")
    private Pilot relatedPilot; // Per penalità specifiche

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
