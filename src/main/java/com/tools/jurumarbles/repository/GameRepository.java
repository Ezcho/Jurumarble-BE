package com.tools.jurumarbles.repository;

import com.tools.jurumarbles.model.GameEntity;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<GameEntity, Integer> {

    void deleteByClientIp(String clientIp);
}
