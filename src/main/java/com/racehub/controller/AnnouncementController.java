package com.racehub.controller;

import com.racehub.model.Announcement;
import com.racehub.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getAnnouncementById(id));
    }

    @GetMapping("/championship/{championshipId}")
    public ResponseEntity<List<Announcement>> getAnnouncementsByChampionship(@PathVariable Long championshipId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByChampionship(championshipId));
    }

    @GetMapping("/race/{raceId}")
    public ResponseEntity<List<Announcement>> getAnnouncementsByRace(@PathVariable Long raceId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByRace(raceId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Announcement>> getAnnouncementsByType(@PathVariable String type) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByType(type));
    }

    @GetMapping("/pinned")
    public ResponseEntity<List<Announcement>> getPinnedAnnouncements() {
        return ResponseEntity.ok(announcementService.getPinnedAnnouncements());
    }

    @PostMapping
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement) {
        Announcement created = announcementService.createAnnouncement(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Announcement> updateAnnouncement(@PathVariable Long id,
                                                           @Valid @RequestBody Announcement announcement) {
        Announcement updated = announcementService.updateAnnouncement(id, announcement);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Announcement deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<Announcement> togglePin(@PathVariable Long id) {
        Announcement updated = announcementService.togglePin(id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<Announcement> togglePublish(@PathVariable Long id) {
        Announcement updated = announcementService.togglePublish(id);
        return ResponseEntity.ok(updated);
    }
}
