package com.tools.jurumarbles.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StartGameRequest {
    private int goal;
    private List<TeamRequest> teams;
    @Getter
    @Setter
    public static class TeamRequest {
        private String name;
    }
}