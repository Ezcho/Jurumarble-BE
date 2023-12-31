
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
        String insertRandomNames = "INSERT INTO " + tableName + " (name) SELECT name FROM map_entity WHERE name NOT IN ('START', 'ISLAND', 'STACK_PUSH', 'STACK_POP', 'GOLDENKEY') ORDER BY RAND() LIMIT 28";
        jdbcTemplate.execute(insertRandomNames);
        String[] predefinedNames = {"START", "ISLAND", "STACK_PUSH", "STACK_POP", "GOLDENKEY", "GOLDENKEY", "GOLDENKEY", "GOLDENKEY"};
        int[] predefinedIds = {1, 15, 8, 22, 4, 11, 18, 25};
        for (int i = 0; i < predefinedNames.length; i++) {
            jdbcTemplate.update(    "UPDATE " + tableName + " SET name = ? WHERE id = ?", predefinedNames[i], predefinedIds[i]);
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

    public int increaseExemptionCard(int gameId) {
        GameEntity game = getGameById(gameId);
        List<TeamEntity> teams = game.getTeams();
        TeamEntity currentTeam = teams.stream()
                .filter(team -> Objects.equals(team.getId(), game.getTurn()))
                .findFirst()
                .orElse(null);
        if (currentTeam != null) {
            currentTeam.increaseExemptionCard();  // Call the method in TeamEntity to increase the exemption card count
            updateGame(gameId, game);
            return currentTeam.getExemptionCard();
        }
        return 0;
    }
//
//    public Map<String, Object> decreaseExemptionCard(int gameId) {
//        GameEntity game = getGameById(gameId);  // 게임 ID로부터 게임 정보를 가져옴
//        List<TeamEntity> teams = game.getTeams();  // 게임에 참여 중인 모든 팀 정보를 가져옴
//        TeamEntity currentTeam = teams.stream()
//                .filter(team -> Objects.equals(team.getId(), game.getTurn()))
//                .findFirst()
//                .orElse(null);  // 현재 턴에 있는 팀을 찾음
//        Map<String, Object> response = new HashMap<>();  // 응답을 담을 Map
//        if (currentTeam != null) {
//            int exemptionCardCount = currentTeam.getExemptionCard();// 현재 팀의 면제권 개수를 가져옴
//            List<Integer> numbers = List.of(1, 15, 8, 4, 11, 18, 25)
//            // 현재 팀이 어떤 칸에 있는지 확인
//            boolean isPredefinedCell = numbers.contains(currentTeam.getPosition());
//            // predefined가 아닌 경우에만 면제권 사용
//            if (!isPredefinedCell) {
//                // 만약 면제권이 남아 있으면 사용 가능
//                if (exemptionCardCount > 0) {
//                    currentTeam.decreaseExemptionCard();  // TeamEntity에서 면제권 감소 메서드 호출
//                    response.put("success", true);
//                    response.put("exemptionCard", currentTeam.getExemptionCard());
//                    updateGame(gameId, game);  // 게임 정보 업데이트
//                } else {
//                    response.put("success", false);
//                    response.put("message", "면제권이 부족합니다.");
//                }
//            } else {
//                response.put("success", false);
//                response.put("message", "면제권을 사용할 수 없는 칸입니다.");
//            }
//        } else {
//            response.put("success", false);
//            response.put("message", "팀을 찾을 수 없습니다.");
//        }
//        return response;  // 면제권 사용 결과를 담은 응답을 반환
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
        //선언
        TeamEntity currentTeam = teams.stream()
                .filter(team -> team.getId() == game.getTurn())
                .findFirst()
                .orElseGet(() -> teams.stream()
                        .min(Comparator.comparingInt(TeamEntity::getId))
                        .orElse(null));
        //현재 팀을 찾는거고.
        System.out.println("현재 팀: "+ currentTeam);
        if (currentTeam != null) {
            int currentPosition = currentTeam.getPosition() + diceValue;
            if(currentPosition >=28){
                currentPosition -= 28;
                currentTeam.setPosition(currentPosition);    //위치가 24초과면 1바퀴 돈걸로 간주하고, 24빼준다.
                currentTeam.setNow(currentTeam.getNow()+1);     //now값 증가시켜준다. //more할때는 얘를 없에자.
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

    /*
    황금열쇠 BACK 요청 들어왔을때 출발점으로 보내는 로직
     */
    public Map<String, Object> goBack(int gameId) {
        GameEntity game = getGameById(gameId);
        if (game == null) return Collections.emptyMap();
        List<TeamEntity> teams = game.getTeams();
        if (teams.isEmpty()) return Collections.emptyMap();

        // 현재 턴의 팀을 찾음
        TeamEntity currentTeam = teams.stream()
                .filter(team -> team.getId() == game.getTurn())
                .findFirst()
                .orElse(null);

        TeamEntity previousTeam = null;
        if (currentTeam == null) {
            // 턴이 설정되지 않았거나 찾을 수 없는 경우, 첫 번째 팀을 현재 팀으로 설정
            currentTeam = teams.get(0);
            game.setTurn(currentTeam.getId());
        } else {

            int currentIndex = teams.indexOf(currentTeam);
            int previousIndex = (currentIndex - 1 + teams.size()) % teams.size();
            previousTeam = teams.get(previousIndex);
            previousTeam.setPosition(1);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", previousTeam.getId());
        response.put("position", previousTeam.getPosition());
        return response;
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