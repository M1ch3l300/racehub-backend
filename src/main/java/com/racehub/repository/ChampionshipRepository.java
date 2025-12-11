package com.racehub.repository;

import com.racehub.model.Championship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChampionshipRepository extends JpaRepository<Championship, Long> {

    List<Championship> findBySeason(Integer season);

    List<Championship> findByStatus(String status);

    Optional<Championship> findByNameAndSeason(String name, Integer season);
}
