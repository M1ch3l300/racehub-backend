package com.racehub.service;

import com.racehub.model.*;
import com.racehub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ChampionshipRepository championshipRepository;
    private final RaceRepository raceRepository;
    private final PilotRepository pilotRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<Announcement> getAllAnnouncementsAdmin() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));
    }

    public List<Announcement> getAnnouncementsByChampionship(Long championshipId) {
        return announcementRepository.findByChampionshipIdOrderByCreatedAtDesc(championshipId);
    }

    public List<Announcement> getAnnouncementsByRace(Long raceId) {
        return announcementRepository.findByRaceIdOrderByCreatedAtDesc(raceId);
    }

    public List<Announcement> getAnnouncementsByType(String type) {
        return announcementRepository.findByTypeOrderByCreatedAtDesc(type);
    }

    public List<Announcement> getPinnedAnnouncements() {
        return announcementRepository.findByPinnedTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public Announcement createAnnouncement(Announcement announcement) {
        // Validate championship if provided
        if (announcement.getChampionship() != null && announcement.getChampionship().getId() != null) {
            Championship championship = championshipRepository.findById(announcement.getChampionship().getId())
                    .orElseThrow(() -> new RuntimeException("Championship not found"));
            announcement.setChampionship(championship);
        }

        // Validate race if provided
        if (announcement.getRace() != null && announcement.getRace().getId() != null) {
            Race race = raceRepository.findById(announcement.getRace().getId())
                    .orElseThrow(() -> new RuntimeException("Race not found"));
            announcement.setRace(race);
        }

        // Validate pilot if provided
        if (announcement.getRelatedPilot() != null && announcement.getRelatedPilot().getId() != null) {
            Pilot pilot = pilotRepository.findById(announcement.getRelatedPilot().getId())
                    .orElseThrow(() -> new RuntimeException("Pilot not found"));
            announcement.setRelatedPilot(pilot);
        }

        return announcementRepository.save(announcement);
    }

    @Transactional
    public Announcement updateAnnouncement(Long id, Announcement announcementDetails) {
        Announcement announcement = getAnnouncementById(id);

        announcement.setType(announcementDetails.getType());
        announcement.setTitle(announcementDetails.getTitle());
        announcement.setContent(announcementDetails.getContent());
        announcement.setPinned(announcementDetails.getPinned());
        announcement.setPublished(announcementDetails.getPublished());

        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcementRepository.delete(announcement);
    }

    @Transactional
    public Announcement togglePin(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setPinned(!announcement.getPinned());
        return announcementRepository.save(announcement);
    }

    @Transactional
    public Announcement togglePublish(Long id) {
        Announcement announcement = getAnnouncementById(id);
        announcement.setPublished(!announcement.getPublished());
        return announcementRepository.save(announcement);
    }
}
