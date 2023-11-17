package com.tools.jurumarbles.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MapEntity {

    @Id
    private int id;
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}