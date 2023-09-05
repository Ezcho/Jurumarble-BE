package com.tools.jurumarbles.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MapEntity {

    @Id
    private int id;
    private String name;
    private String description;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {

        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
}
