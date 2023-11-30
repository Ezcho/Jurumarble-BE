package com.tools.jurumarbles.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class GoldEntity {
    @Id
    private Long id;
    private String name;
}
