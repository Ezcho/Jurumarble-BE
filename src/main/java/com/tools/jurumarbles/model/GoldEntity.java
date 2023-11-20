package com.tools.jurumarbles.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GoldEntity {
    @Id
    private Long id;
    private String name;
}
