package com.racehub.controller;

import com.racehub.model.Pilot;
import com.racehub.service.PilotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pilots")
@RequiredArgsConstructor
public class PilotController {
    
    private final PilotService pilotService;
    
    @GetMapping
    public ResponseEntity<List<Pilot>> getAllPilots() {
        return ResponseEntity.ok(pilotService.getAllPilots());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        return ResponseEntity.ok(pilotService.getPilotById(id));
    }
    
    @PostMapping
    public ResponseEntity<Pilot> createPilot(@Valid @RequestBody Pilot pilot) {
        Pilot createdPilot = pilotService.createPilot(pilot);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPilot);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long id, 
                                             @Valid @RequestBody Pilot pilot) {
        Pilot updatedPilot = pilotService.updatePilot(id, pilot);
        return ResponseEntity.ok(updatedPilot);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Pilot deleted successfully");
        return ResponseEntity.ok(response);
    }
}
