package com.racehub.controller;

import com.racehub.dto.*;
import com.racehub.model.Race;
import com.racehub.model.RaceResult;
import com.racehub.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/races")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;

    @GetMapping
    public ResponseEntity<List<RaceDTO>> getAllRaces() {
        List<RaceDTO> dtos = raceService.getAllRaces().stream()
                .map(RaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceDTO> getRaceById(@PathVariable Long id) {
        return ResponseEntity.ok(new RaceDTO(raceService.getRaceById(id)));
    }

    @GetMapping("/championship/{championshipId}")
    public ResponseEntity<List<RaceDTO>> getRacesByChampionship(@PathVariable Long championshipId) {
        List<RaceDTO> dtos = raceService.getRacesByChampionship(championshipId).stream()
                .map(RaceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<Race> createRace(@RequestBody Race race) {
        return ResponseEntity.ok(raceService.createRace(race));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.ok(raceService.updateRace(id, race));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
        return ResponseEntity.ok().build();
    }

    // RESULTS - USA DTO
    @PostMapping("/{raceId}/results")
    public ResponseEntity<RaceResult> addRaceResult(@PathVariable Long raceId, @RequestBody RaceResult result) {
        return ResponseEntity.ok(raceService.addRaceResult(raceId, result));
    }

    @GetMapping("/{raceId}/results")
    public ResponseEntity<List<RaceResultDTO>> getRaceResults(@PathVariable Long raceId) {
        List<RaceResultDTO> dtos = raceService.getRaceResults(raceId).stream()
                .map(RaceResultDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/results/{resultId}")
    public ResponseEntity<Void> deleteRaceResult(@PathVariable Long resultId) {
        raceService.deleteRaceResult(resultId);
        return ResponseEntity.ok().build();
    }

    // STANDINGS - USA DTO
    @GetMapping("/championship/{championshipId}/standings")
    public ResponseEntity<StandingsDTO> getChampionshipStandings(@PathVariable Long championshipId) {
        Map<String, Object> standings = raceService.getChampionshipStandings(championshipId);

        StandingsDTO dto = new StandingsDTO();
        dto.setChampionship(new ChampionshipDTO(
                (com.racehub.model.Championship) standings.get("championship")
        ));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> standingsList = (List<Map<String, Object>>) standings.get("standings");

        List<PilotStandingDTO> standingsDTOs = standingsList.stream()
                .map(entry -> new PilotStandingDTO(
                        new PilotDTO((com.racehub.model.Pilot) entry.get("pilot")),
                        (Integer) entry.get("points")
                ))
                .collect(Collectors.toList());

        dto.setStandings(standingsDTOs);

        return ResponseEntity.ok(dto);
    }
}
