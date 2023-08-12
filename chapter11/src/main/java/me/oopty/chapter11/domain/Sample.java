package me.oopty.chapter11.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Sample {

    @Id @GeneratedValue
    private Long id;

    private String name;

    public Sample() {
    }

    public Sample(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Sample(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
