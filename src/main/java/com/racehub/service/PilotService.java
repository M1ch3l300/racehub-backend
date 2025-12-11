package com.racehub.service;

import com.racehub.model.Pilot;
import com.racehub.repository.PilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PilotService {
    
    private final PilotRepository pilotRepository;
    
    public List<Pilot> getAllPilots() {
        return pilotRepository.findAll();
    }
    
    public Pilot getPilotById(Long id) {
        return pilotRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pilot not found with id: " + id));
    }
    
    @Transactional
    public Pilot createPilot(Pilot pilot) {
        if (pilotRepository.existsByRacingNumber(pilot.getRacingNumber())) {
            throw new RuntimeException("Racing number already exists: " + pilot.getRacingNumber());
        }
        return pilotRepository.save(pilot);
    }
    
    @Transactional
    public Pilot updatePilot(Long id, Pilot pilotDetails) {
        Pilot pilot = getPilotById(id);
        
        // Check if racing number is being changed and if it's already taken
        if (!pilot.getRacingNumber().equals(pilotDetails.getRacingNumber()) 
            && pilotRepository.existsByRacingNumber(pilotDetails.getRacingNumber())) {
            throw new RuntimeException("Racing number already exists: " + pilotDetails.getRacingNumber());
        }
        
        pilot.setName(pilotDetails.getName());
        pilot.setTeam(pilotDetails.getTeam());
        pilot.setRacingNumber(pilotDetails.getRacingNumber());
        pilot.setNationality(pilotDetails.getNationality());
        
        return pilotRepository.save(pilot);
    }
    
    @Transactional
    public void deletePilot(Long id) {
        Pilot pilot = getPilotById(id);
        pilotRepository.delete(pilot);
    }
}
