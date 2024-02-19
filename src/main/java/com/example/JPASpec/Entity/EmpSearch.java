package com.example.JPASpec.Entity;

import lombok.Data;

@Data
public class EmpSearch {
    private String firstName;
    private String depName;

    public EmpSearch(String firstName, String depName) {
        this.firstName = firstName;
        this.depName = depName;
    }

    public EmpSearch() {
    }
}
