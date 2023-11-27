
package com.tools.jurumarbles.service;

import com.tools.jurumarbles.model.GameEntity;
import com.tools.jurumarbles.model.TeamEntity;
import com.tools.jurumarbles.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class GameService {
    @Autowired
    private GameRepository repository;
    public Iterable<GameEntity> getAllGames() {
        return repository.findAll();
    }
    public GameEntity startNewGame(int goal, String clientIp, List<TeamEntity> teams) {
        GameEntity newGame = new GameEntity();
        newGame.setGameId(generateRandomGameId());
        newGame.setClientIp(clientIp);
        newGame.setStack(0); //stack을 0으로 초기화
        newGame.setGoal(goal); //goal 설정
        newGame.setTurn(null); //turn을 null로 설정
        newGame.setTeams(teams); //팀 설정

        createAndPopulateGameTable(newGame.getGameId());
        repository.save(newGame);
        return newGame;
    }
    public GameEntity getGameById(int gameId) {
        return repository.findById(gameId).orElse(null);
    }
    public GameEntity updateGame(int gameId, GameEntity game) {
        if (repository.existsById(gameId)) {
            game.setGameId(gameId);
            return repository.save(game);
        }
        return null;
    }

    private Integer generateRandomGameId() {
        Random rand = new Random();
        return 100_000 + rand.nextInt(900_000);
    }

    /*
    얘네 맵 만들어서 front로 반환하는거, 테이블명은 항상 "GXXXXXX"의 형태를 따른다.
    SQL 쿼리만 있어서 딱히 설명할건 없는듯.
    */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public void createAndPopulateGameTable(int gameId) {
        String tableName = "G" + gameId;
        jdbcTemplate.execute("CREATE TABLE " + tableName + " (id INT AUTO_INCREMENT, name VARCHAR(255), PRIMARY KEY (id))");
        String insertRandomNames = "INSERT INTO " + tableName + " (name) SELECT name FROM map_entity WHERE name NOT IN ('START', 'ISLAND', 'STACK_PUSH', 'STACK_POP', 'GOLDENKEY') ORDER BY RAND() LIMIT 24";
        jdbcTemplate.execute(insertRandomNames);
        String[] predefinedNames = {"START", "ISLAND", "STACK_PUSH", "STACK_POP", "GOLDENKEY", "GOLDENKEY", "GOLDENKEY", "GOLDENKEY"};
        int[] predefinedIds = {1, 7, 13, 19, 4, 10, 16, 22};
        for (int i = 0; i < predefinedNames.length; i++) {
            jdbcTemplate.update("UPDATE " + tableName + " SET name = ? WHERE id = ?", predefinedNames[i], predefinedIds[i]);
        }
    }
    /*
    스택관련 함수들, /api/v1/status/gameId/stack_push 혹은
    /api/v1/status/${gameId}/stack_pop 요청이 들어왔을 때 수행하는 함수
     */

    public int increaseStack(int gameId) {
        GameEntity game = getGameById(gameId);
        int currentStack = game.getStack();
        game.setStack(currentStack + 1);
        updateGame(gameId, game);
        return game.getStack();
    }
    public Map<String, Object>resetStack(int gameId) {
        GameEntity game = getGameById(gameId);
        int currentStack = game.getStack();
        game.setStack(0);
        updateGame(gameId, game);
        TeamEntity currentTeam = game.getTeams().stream()
                .filter(team -> Objects.equals(team.getId(), game.getTurn()))
                .findFirst()
                .orElse(null);
        Map<String, Object> response = new HashMap<>();
        if (currentTeam != null) {
            response.put("id", currentTeam.getId());
            response.put("name", currentTeam.getName());
            response.put("position", currentTeam.getPosition());
            response.put("stack", currentStack);
        }
        return response;
    }
//    public static int increaseExemptionCard(int gameId) {
//        GameEntity game = getGameById(gameId);
//        int currentStack = game.getStack();
//        game.setStack(currentStack + 1);
//        updateGame(gameId, game);
//        return game.getStack();
//    }

    /*
    rollDice 함수
    최초value로 첫번째 팀의 id를 설정한다음,
    api/v1/status/${gameId}/dice?value=n이라는 요청이들어오면,
    해당팀의 id와 name, position + n한 값을 response하고
    turn값을 다음팀의 id로 함
    ++ 추가로, 코드썻는데 겁나길어서 걍 gpt한테 줄여달라함 그결과 코드가 1/3으로 줄어드는 매직ㅋㅋ
    turn 값이 최초에는 null인데 (왜냐면 게임을 생성하고, 팀을 추가하는 로직이라 -> 수정하고싶은데 귀찮다..)
    */

    public Map<String, Object> rollDice(int gameId, int diceValue) {
        GameEntity game = getGameById(gameId);
        List<TeamEntity> teams = game.getTeams();
        TeamEntity currentTeam = teams.stream()
                .filter(team -> team.getId() == game.getTurn())
                .findFirst()
                .orElseGet(() -> teams.stream()
                        .min(Comparator.comparingInt(TeamEntity::getId))
                        .orElse(null));
        System.out.println("현재 팀: "+ currentTeam);
        if (currentTeam != null) {
            int currentPosition = currentTeam.getPosition() + diceValue;
            if(currentPosition >=25){
                currentTeam.setPosition(currentPosition-24);    //위치가 24초과면 1바퀴 돈걸로 간주하고, 24빼준다.
                currentTeam.setNow(currentTeam.getNow()+1);     //now값 증가시켜준다.
                if(currentTeam.getNow() == game.getGoal()){     //설정한 바퀴수를 다 돈 경우
                    return endGame(gameId, currentTeam);        //endGame메서드 실행
                }
            }
            currentTeam.setPosition(currentPosition);                               //일반적인 경우
            int nextTurnIndex = (teams.indexOf(currentTeam) + 1) % teams.size();    //다음 순서 설정
            game.setTurn(teams.get(nextTurnIndex).getId());                         //
            updateGame(gameId, game);
        }
        Map<String, Object> response = new HashMap<>();
        if (currentTeam != null) {
            response.put("id", currentTeam.getId());
            response.put("name", currentTeam.getName());
            response.put("position", currentTeam.getPosition());
        }
        return response;
    }

    /*
        게임종료 deleteGame, deleteGameTable
    */
    public void deleteGame(int id) {
        deleteGameTable(id);
        repository.deleteById(id);
    }
    private void deleteGameTable(int gameId) {
        String tableName = "G" + gameId;
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
    }
    //얘는 ip기반 종료하는것,
    public void deleteByClientIp(String clientIp) {
        List<GameEntity> games = repository.findByClientIp(clientIp);
        for (GameEntity game : games) {
            deleteGameTable(game.getGameId());
        }
        repository.deleteByClientIp(clientIp);
    }
    public Map<String, Object> endGame(int gameId, TeamEntity currentTeam){
        deleteGame(gameId);
        Map<String, Object> response = new HashMap<>();
        response.put("wins: ", currentTeam.getName());
        return response;
    }



}