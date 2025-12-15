package com.racehub.controller;

import com.racehub.model.Championship;
import com.racehub.model.Pilot;
import com.racehub.service.ChampionshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/championships")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ChampionshipController {

    private final ChampionshipService championshipService;

    @GetMapping
    public ResponseEntity<List<Championship>> getAllChampionships() {
        return ResponseEntity.ok(championshipService.getAllChampionships());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Championship> getChampionshipById(@PathVariable Long id) {
        return ResponseEntity.ok(championshipService.getChampionshipById(id));
    }

    @GetMapping("/season/{season}")
    public ResponseEntity<List<Championship>> getChampionshipsBySeason(@PathVariable Integer season) {
        return ResponseEntity.ok(championshipService.getChampionshipsBySeason(season));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Championship>> getChampionshipsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(championshipService.getChampionshipsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Championship> createChampionship(@Valid @RequestBody Championship championship) {
        Championship created = championshipService.createChampionship(championship);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Championship> updateChampionship(@PathVariable Long id,
                                                           @Valid @RequestBody Championship championship) {
        Championship updated = championshipService.updateChampionship(id, championship);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteChampionship(@PathVariable Long id) {
        championshipService.deleteChampionship(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Championship deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{championshipId}/pilots/{pilotId}")
    public ResponseEntity<Championship> addPilotToChampionship(@PathVariable Long championshipId,
                                                               @PathVariable Long pilotId) {
        Championship updated = championshipService.addPilotToChampionship(championshipId, pilotId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{championshipId}/pilots/{pilotId}")
    public ResponseEntity<Map<String, String>> removePilotFromChampionship(@PathVariable Long championshipId,
                                                                           @PathVariable Long pilotId) {
        championshipService.removePilotFromChampionship(championshipId, pilotId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Pilot removed from championship successfully");
        return ResponseEntity.ok(response);
    }
    // 🔥 AGGIUNGI QUESTO METODO!
    @GetMapping("/{id}/pilots")
    public ResponseEntity<Set<Pilot>> getChampionshipPilots(@PathVariable Long id) {
        Championship championship = championshipService.getChampionshipById(id);
        return ResponseEntity.ok(championship.getPilots());
    }

}
