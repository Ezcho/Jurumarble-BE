package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.dto.DiceRequest;
import com.tools.jurumarbles.dto.StartGameRequest;
import com.tools.jurumarbles.model.GameEntity;
import com.tools.jurumarbles.model.TeamEntity;
import com.tools.jurumarbles.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/status")
public class GameController {

    @Autowired
    private GameService service;

    @GetMapping// api/v1/status
    public Iterable<GameEntity> getAllGames() {
        return service.getAllGames();
    }

    @GetMapping("/{id}")//특정게임 상태조회
    public GameEntity getGameById(@PathVariable int id) {
        return service.getGameById(id);
    }
    @PostMapping("/start")  // 게임시작 request 처리
    public Map<String, Object> startGame(@RequestBody StartGameRequest request, HttpServletRequest httpRequest) {
        //DTO에서 goal과 팀 정보 추출
        int goal = request.getGoal();
        List<TeamEntity> teams = request.getTeams().stream()
                .map(teamRequest -> {
                    TeamEntity team = new TeamEntity();
                    team.setName(teamRequest.getName());
                    team.setPosition(0);
                    team.setNow(0);
//                    team.setSudo(0);
                    return team;
                })
                .collect(Collectors.toList());
        GameEntity game = service.startNewGame(goal, httpRequest.getRemoteAddr(), teams);
        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("gameId", game.getGameId());
        response.put("goal", game.getGoal());
        return response;
    }

    @PostMapping("/{gameId}/dice")
    public Map<String, Object> rollDice(@PathVariable int gameId, @RequestBody DiceRequest diceRollRequest) {
        System.out.println("Dice!: "+ diceRollRequest.getValue());
        return service.rollDice(gameId, diceRollRequest.getValue());
    }

    @PostMapping("/{gameId}/stack_push")//축척주 누적
    public Map<String, Integer> increaseStack(@PathVariable int gameId) {
        int updatedStack = service.increaseStack(gameId);
        Map<String, Integer> response = new HashMap<>();
        response.put("stack", updatedStack);
        return response;
    }
    @GetMapping("/{gameId}/stack_pop")//축척주 초기화
    public ResponseEntity<Map<String, Object>> resetStack(@PathVariable int gameId) {
        Map<String, Object> response = service.resetStack(gameId);
        return ResponseEntity.ok(response);
    }
    // exemptionCard 증가
//    @PostMapping("/{gameId}/excemption_push")
//    public ResponseEntity <Map<String, Object>> increaseExemptionCard(@PathVariable int gameId) {
//        int currentExemptionCard = GameService.increaseExemptionCard(gameId);
//        Map<String, Integer> response = new HashMap<>();
//        response.put("exception_card", );
//        return response;
//    }

    // exemptionCard 감소
//    @PostMapping("/{gameId}/{teamId}/excemption_pop")
//    public ResponseEntity<?> decreaseExemptionCard(@PathVariable Long gameId, @PathVariable Long teamId) {
//        GameService.decreaseExemptionCard(gameId, teamId);
//        return ResponseEntity.ok().build();
//    }
    @PostMapping("/end")//ip기반 겜종료
    public void endGame(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        service.deleteByClientIp(clientIp);
    }
    @DeleteMapping("/{id}") //gameId기반 게임종료
    public void deleteGame(@PathVariable int id) {
        service.deleteGame(id);
    }


    @PutMapping("/{id}")//팀 생성 PUT 메서드 사용안하는중,
    public GameEntity updateGame(@PathVariable int id, @RequestBody GameEntity game) {
        return service.updateGame(id, game);
    }
}
