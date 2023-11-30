package com.tools.jurumarbles.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer position;
    private Integer now = 0;
    private Integer exemptionCard = 0;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "game_id")
    private GameEntity game;
    public Integer getId() {
        return id;
    }
    public GameEntity getGame() {
        return game;
    }

    public Integer getExemptionCard(){ return exemptionCard; }

    public void increaseExemptionCard() {
        this.exemptionCard++;
    }

    public void decreaseExemptionCard() {
        this.exemptionCard--;
    }
}
