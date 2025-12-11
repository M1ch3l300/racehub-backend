package com.racehub.service;

import com.racehub.model.Championship;
import com.racehub.model.Pilot;
import com.racehub.repository.ChampionshipRepository;
import com.racehub.repository.PilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;
    private final PilotRepository pilotRepository;

    public List<Championship> getAllChampionships() {
        return championshipRepository.findAll();
    }

    public Championship getChampionshipById(Long id) {
        return championshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Championship not found with id: " + id));
    }

    public List<Championship> getChampionshipsBySeason(Integer season) {
        return championshipRepository.findBySeason(season);
    }

    public List<Championship> getChampionshipsByStatus(String status) {
        return championshipRepository.findByStatus(status);
    }

    @Transactional
    public Championship createChampionship(Championship championship) {
        // Validate status
        if (!List.of("UPCOMING", "ACTIVE", "COMPLETED").contains(championship.getStatus())) {
            throw new RuntimeException("Invalid status. Must be: UPCOMING, ACTIVE, or COMPLETED");
        }
        return championshipRepository.save(championship);
    }

    @Transactional
    public Championship updateChampionship(Long id, Championship championshipDetails) {
        Championship championship = getChampionshipById(id);

        championship.setName(championshipDetails.getName());
        championship.setSeason(championshipDetails.getSeason());
        championship.setStatus(championshipDetails.getStatus());
        championship.setDescription(championshipDetails.getDescription());
        championship.setMaxPilots(championshipDetails.getMaxPilots());
        championship.setPointsSystem(championshipDetails.getPointsSystem());

        return championshipRepository.save(championship);
    }

    @Transactional
    public void deleteChampionship(Long id) {
        Championship championship = getChampionshipById(id);
        championshipRepository.delete(championship);
    }

    @Transactional
    public Championship addPilotToChampionship(Long championshipId, Long pilotId) {
        Championship championship = getChampionshipById(championshipId);
        Pilot pilot = pilotRepository.findById(pilotId)
                .orElseThrow(() -> new RuntimeException("Pilot not found with id: " + pilotId));

        // Check if championship is full
        if (championship.getPilots().size() >= championship.getMaxPilots()) {
            throw new RuntimeException("Championship is full. Max pilots: " + championship.getMaxPilots());
        }

        championship.getPilots().add(pilot);
        return championshipRepository.save(championship);
    }

    @Transactional
    public Championship removePilotFromChampionship(Long championshipId, Long pilotId) {
        Championship championship = getChampionshipById(championshipId);
        Pilot pilot = pilotRepository.findById(pilotId)
                .orElseThrow(() -> new RuntimeException("Pilot not found with id: " + pilotId));

        championship.getPilots().remove(pilot);
        return championshipRepository.save(championship);
    }
}
