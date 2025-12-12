package com.racehub.repository;

import com.racehub.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    List<Race> findByChampionshipIdOrderByRoundAsc(Long championshipId);

    Optional<Race> findByChampionshipIdAndRound(Long championshipId, Integer round);

    List<Race> findByStatus(String status);
}
