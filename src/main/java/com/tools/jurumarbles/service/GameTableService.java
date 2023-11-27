package com.tools.jurumarbles.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GameTableService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public List<Map<String, Object>> getGameTableData(int gameId) {
        String tableName = "G" + gameId;
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
    }
    public List<Map<String, Object>> getGameTableDataDetail(int gameId, int id) {
        String tableName = "G" + gameId;
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName+ "WHERE id ="+id);
    }
}
