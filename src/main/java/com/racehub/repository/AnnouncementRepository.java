package com.racehub.repository;

import com.racehub.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByChampionshipIdOrderByCreatedAtDesc(Long championshipId);

    List<Announcement> findByRaceIdOrderByCreatedAtDesc(Long raceId);

    List<Announcement> findByTypeOrderByCreatedAtDesc(String type);

    List<Announcement> findByPinnedTrueOrderByCreatedAtDesc();

    List<Announcement> findByPublishedTrueOrderByCreatedAtDesc();

    List<Announcement> findAllByOrderByCreatedAtDesc();
}
