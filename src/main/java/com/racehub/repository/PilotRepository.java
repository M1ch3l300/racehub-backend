package com.racehub.repository;

import com.racehub.model.Pilot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
    
    Optional<Pilot> findByRacingNumber(Integer racingNumber);
    
    boolean existsByRacingNumber(Integer racingNumber);
}
