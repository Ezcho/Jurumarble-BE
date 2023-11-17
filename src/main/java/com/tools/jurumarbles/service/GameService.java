
package com.tools.jurumarbles.service;

import com.tools.jurumarbles.model.GameEntity;
import com.tools.jurumarbles.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class GameService {

    @Autowired
    private GameRepository repository;

    public GameEntity createGame(GameEntity game) {
        game.setGameId(generateRandomGameId());
        GameEntity savedGame = repository.save(game);
        createAndPopulateGameTable(savedGame.getGameId());
        return repository.save(game);
    }

    public Iterable<GameEntity> getAllGames() {
        return repository.findAll();
    }
    public GameEntity getGameById(int id) {
        return repository.findById(id).orElse(null);
    }
    public GameEntity updateGame(int id, GameEntity game) {
        if (repository.existsById(id)) {
            game.setGameId(id);
            return repository.save(game);
        }
        return null;
    }
    public void deleteGame(int id) {
        deleteRelatedGameTable(id);
        repository.deleteById(id);

    }
    public void deleteByClientIp(String clientIp) {
        List<GameEntity> games = repository.findByClientIp(clientIp);
        for (GameEntity game : games) {
            deleteRelatedGameTable(game.getGameId());
        }
        repository.deleteByClientIp(clientIp);
    }
    private Integer generateRandomGameId() {
        Random rand = new Random();
        return 100_000 + rand.nextInt(900_000); // 정수랜덤생성 6자리
    }
    private void deleteRelatedGameTable(int gameId) {
        String tableName = "G" + gameId;
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
    }
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createAndPopulateGameTable(int gameId) {
        String tableName = "G" + gameId;
        jdbcTemplate.execute("CREATE TABLE " + tableName + " (id INT AUTO_INCREMENT, name VARCHAR(255), PRIMARY KEY (id))");
        String insertSql = "INSERT INTO " + tableName + " (name) SELECT name FROM map_entity ORDER BY RAND() LIMIT 20";
        jdbcTemplate.execute(insertSql);
    }
}