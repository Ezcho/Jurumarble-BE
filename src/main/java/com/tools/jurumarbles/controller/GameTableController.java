package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.service.GameTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/game-table")
public class GameTableController {

    @Autowired
    private GameTableService gameTableService;

    @GetMapping("/{gameId}")
    public List<Map<String, Object>> getGameTableData(@PathVariable int gameId) {
        return gameTableService.getGameTableData(gameId);
    }
    @GetMapping("/{gameId}/{id}")
    public List<Map<String, Object>> getGameTableData(@PathVariable int gameId, @PathVariable int id) {
        return gameTableService.getGameTableDataDetail(gameId, id);
    }
}
