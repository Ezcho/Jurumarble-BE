package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.model.GameEntity;
import com.tools.jurumarbles.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    @Autowired
    private GameService service;

    @PostMapping("/start")
    public GameEntity startGame(@RequestBody GameEntity game, HttpServletRequest request) {
        game.setClientIp(request.getRemoteAddr()); // Set the client IP
        return service.createGame(game);
    }

    @GetMapping
    public Iterable<GameEntity> getAllGames() {
        return service.getAllGames();
    }

    @GetMapping("/{id}")
    public GameEntity getGameById(@PathVariable int id) {
        return service.getGameById(id);
    }

    @PutMapping("/{id}")
    public GameEntity updateGame(@PathVariable int id, @RequestBody GameEntity game) {
        return service.updateGame(id, game);
    }

    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable int id) {
        service.deleteGame(id);
    }

    @PostMapping("/end")
    public void endGame(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr(); // Get the client IP
        service.deleteByClientIp(clientIp);
    }
}
