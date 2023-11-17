package com.tools.jurumarbles.repository;

import com.tools.jurumarbles.model.GameEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<GameEntity, Integer> {

    void deleteByClientIp(String clientIp);
    List<GameEntity> findByClientIp(String clientIp);
}
