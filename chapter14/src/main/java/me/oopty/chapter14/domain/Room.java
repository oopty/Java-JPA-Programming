package me.oopty.chapter14.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Room {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Team team;

    public Long getId() {
        return id;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
