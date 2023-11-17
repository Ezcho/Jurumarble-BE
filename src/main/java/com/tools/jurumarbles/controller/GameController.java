package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.model.GameEntity;
import com.tools.jurumarbles.model.TeamEntity;
import com.tools.jurumarbles.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/status")
public class GameController {

    @Autowired
    private GameService service;

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

    @PostMapping("/start")
    public Map<String, Object> startGame(@RequestBody GameEntity game, HttpServletRequest request) {
        game.setClientIp(request.getRemoteAddr());
        GameEntity savedGame = service.createGame(game);

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", savedGame.getGameId());
        response.put("clientIp", savedGame.getClientIp());
        response.put("stack", savedGame.getStack());

        List<Map<String, Object>> teams = new ArrayList<>();
        for (TeamEntity team : savedGame.getTeams()) {
            Map<String, Object> teamInfo = new HashMap<>();
            teamInfo.put("name", team.getName());
            teamInfo.put("position", team.getPosition());
            teams.add(teamInfo);
        }
        response.put("team", teams);
        return response;
    }

    @PostMapping("/end")
    public void endGame(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        service.deleteByClientIp(clientIp);
    }
}
