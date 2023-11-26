package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.model.GoldEntity;
import com.tools.jurumarbles.repository.GoldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
public class GoldController {

    private final GoldRepository goldRepository;
    @Autowired
    public GoldController(GoldRepository goldRepository) {
        this.goldRepository = goldRepository;
    }
    @GetMapping("/api/v1/goldKey")
    public String getRandomGold(Model model) {
        // Get all entities from the table
        List<GoldEntity> allEntities = goldRepository.findAll();
        // Check if there are entities in the table
        if(allEntities.isEmpty()) {
            model.addAttribute("errorMessage", "No entities found in the 'gold_entity'");
        } else {
            // Get a random index
            int randomIndex = new Random().nextInt(allEntities.size());
            // Get the randomly selected entity
            GoldEntity randomEntity = allEntities.get(randomIndex);
            // Add the entity to the model for rendering in the view
            model.addAttribute("randomEntity", randomEntity);
        }
        // Return the name of the Thymeleaf template
        return "goldKey";
    }
}
