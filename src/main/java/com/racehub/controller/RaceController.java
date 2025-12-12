package com.racehub.controller;

import com.racehub.model.Race;
import com.racehub.model.RaceResult;
import com.racehub.service.RaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/races")
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;

    @GetMapping
    public ResponseEntity<List<Race>> getAllRaces() {
        return ResponseEntity.ok(raceService.getAllRaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        return ResponseEntity.ok(raceService.getRaceById(id));
    }

    @GetMapping("/championship/{championshipId}")
    public ResponseEntity<List<Race>> getRacesByChampionship(@PathVariable Long championshipId) {
        return ResponseEntity.ok(raceService.getRacesByChampionship(championshipId));
    }

    @PostMapping
    public ResponseEntity<Race> createRace(@Valid @RequestBody Race race) {
        Race created = raceService.createRace(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable Long id, @Valid @RequestBody Race race) {
        Race updated = raceService.updateRace(id, race);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Race deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{raceId}/results")
    public ResponseEntity<RaceResult> addRaceResult(@PathVariable Long raceId,
                                                    @Valid @RequestBody RaceResult result) {
        RaceResult created = raceService.addRaceResult(raceId, result);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{raceId}/results")
    public ResponseEntity<List<RaceResult>> getRaceResults(@PathVariable Long raceId) {
        return ResponseEntity.ok(raceService.getRaceResults(raceId));
    }

    @GetMapping("/championship/{championshipId}/standings")
    public ResponseEntity<Map<String, Object>> getChampionshipStandings(@PathVariable Long championshipId) {
        return ResponseEntity.ok(raceService.getChampionshipStandings(championshipId));
    }
}
