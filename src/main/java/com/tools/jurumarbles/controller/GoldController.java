package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.model.GoldEntity;
import com.tools.jurumarbles.repository.GoldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
public class GoldController {

    private final GoldRepository goldRepository;

    @Autowired
    public GoldController(GoldRepository goldRepository) {
        this.goldRepository = goldRepository;
    }

    @GetMapping("/api/v1/goldKey")
    public ResponseEntity<?> getRandomGold() {
        List<GoldEntity> allEntities = goldRepository.findAll();
        if (allEntities.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            int randomIndex = new Random().nextInt(allEntities.size());
            GoldEntity randomEntity = allEntities.get(randomIndex);
            return ResponseEntity.ok(randomEntity);
        }
    }
}
