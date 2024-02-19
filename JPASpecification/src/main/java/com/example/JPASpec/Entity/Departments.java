package com.example.JPASpec.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Departments {
    @Id
    private long id;

    private String depName;

    public Departments() {
    }

    public Departments(long id, String depName) {
        this.id = id;
        this.depName = depName;
    }
}
