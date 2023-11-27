package com.tools.jurumarbles.model;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "status")
public class GameEntity {

    @Id
    private Integer gameId;
    private String clientIp;
    private Integer stack;
    private Integer goal;
    private Integer turn;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeamEntity> teams;
    public void setTeams(List<TeamEntity> teams) {
        this.teams = teams;
        for (TeamEntity team : teams) {
            team.setGame(this);
        }
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getStack() {
        return stack;
    }

    public void setStack(Integer stack) {
        this.stack = stack;
    }

    public List<TeamEntity> getTeams() {
        return teams;
    }

    public void setTurn(Integer turn){this.turn = turn;}
    public Integer getTurn(){return turn;}
    public void setGoal(int goal) {this.goal = goal;}
    public int getGoal(){return goal;}
}
