package com.racehub.repository;

import com.racehub.model.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {

    List<RaceResult> findByRaceIdOrderByPositionAsc(Long raceId);

    List<RaceResult> findByPilotId(Long pilotId);

    @Query("SELECT r FROM RaceResult r WHERE r.race.championship.id = :championshipId ORDER BY r.pilot.id, r.race.round")
    List<RaceResult> findByChampionshipId(@Param("championshipId") Long championshipId);
}
