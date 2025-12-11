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
@Table(name = "pilots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pilot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Team is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String team;
    
    @NotNull(message = "Racing number is required")
    @Min(value = 1, message = "Racing number must be positive")
    @Max(value = 999, message = "Racing number must be max 999")
    @Column(nullable = false, unique = true)
    private Integer racingNumber;
    
    @Size(max = 50)
    private String nationality;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
