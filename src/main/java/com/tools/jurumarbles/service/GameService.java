package com.tools.jurumarbles.service;

import com.tools.jurumarbles.model.GameEntity;
import com.tools.jurumarbles.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class GameService {

    @Autowired
    private GameRepository repository;

    public GameEntity createGame(GameEntity game) {
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
        repository.deleteById(id);
    }

    public void deleteByClientIp(String clientIp) {
        repository.deleteByClientIp(clientIp);
        System.out.println(clientIp);
    }
}
