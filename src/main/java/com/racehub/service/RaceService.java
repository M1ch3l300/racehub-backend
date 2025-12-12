package com.racehub.service;

import com.racehub.model.*;
import com.racehub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RaceService {

    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;
    private final ChampionshipRepository championshipRepository;
    private final PilotRepository pilotRepository;

    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    public Race getRaceById(Long id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Race not found with id: " + id));
    }

    public List<Race> getRacesByChampionship(Long championshipId) {
        return raceRepository.findByChampionshipIdOrderByRoundAsc(championshipId);
    }

    @Transactional
    public Race createRace(Race race) {
        // Validate championship exists
        Championship championship = championshipRepository.findById(race.getChampionship().getId())
                .orElseThrow(() -> new RuntimeException("Championship not found"));

        // Check if round already exists
        Optional<Race> existing = raceRepository.findByChampionshipIdAndRound(
                championship.getId(), race.getRound());
        if (existing.isPresent()) {
            throw new RuntimeException("Round " + race.getRound() + " already exists for this championship");
        }

        race.setChampionship(championship);
        return raceRepository.save(race);
    }
    public void deleteRaceResult(Long resultId) {
        raceResultRepository.deleteById(resultId);
    }

    @Transactional
    public Race updateRace(Long id, Race raceDetails) {
        Race race = getRaceById(id);

        race.setRound(raceDetails.getRound());
        race.setCircuit(raceDetails.getCircuit());
        race.setRaceDate(raceDetails.getRaceDate());
        race.setStatus(raceDetails.getStatus());
        race.setNotes(raceDetails.getNotes());

        return raceRepository.save(race);
    }

    @Transactional
    public void deleteRace(Long id) {
        Race race = getRaceById(id);
        raceRepository.delete(race);
    }

    @Transactional
    public RaceResult addRaceResult(Long raceId, RaceResult result) {
        Race race = getRaceById(raceId);
        Pilot pilot = pilotRepository.findById(result.getPilot().getId())
                .orElseThrow(() -> new RuntimeException("Pilot not found"));

        // Calculate points based on position and championship points system
        Integer points = calculatePoints(race.getChampionship(), result.getPosition(), result.getStatus());

        result.setRace(race);
        result.setPilot(pilot);
        result.setPoints(points);

        return raceResultRepository.save(result);
    }

    public List<RaceResult> getRaceResults(Long raceId) {
        return raceResultRepository.findByRaceIdOrderByPositionAsc(raceId);
    }

    public Map<String, Object> getChampionshipStandings(Long championshipId) {
        Championship championship = championshipRepository.findById(championshipId)
                .orElseThrow(() -> new RuntimeException("Championship not found"));

        List<RaceResult> allResults = raceResultRepository.findByChampionshipId(championshipId);

        // Group by pilot and sum points
        Map<Pilot, Integer> pilotPoints = allResults.stream()
                .collect(Collectors.groupingBy(
                        RaceResult::getPilot,
                        Collectors.summingInt(RaceResult::getPoints)
                ));

        // Convert to sorted list
        List<Map<String, Object>> standings = pilotPoints.entrySet().stream()
                .sorted(Map.Entry.<Pilot, Integer>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> standing = new HashMap<>();
                    standing.put("pilot", entry.getKey());
                    standing.put("points", entry.getValue());
                    return standing;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("championship", championship);
        response.put("standings", standings);

        return response;
    }

    private Integer calculatePoints(Championship championship, Integer position, String status) {
        if (!"FINISHED".equals(status)) {
            return 0;
        }

        String pointsSystem = championship.getPointsSystem();
        String[] pointsArray = pointsSystem.split(",");

        if (position <= pointsArray.length) {
            return Integer.parseInt(pointsArray[position - 1].trim());
        }

        return 0;
    }
}
