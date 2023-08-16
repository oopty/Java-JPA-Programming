package me.oopty.chapter15.domain;

import me.oopty.chapter15.visitor.Visitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "MOVIE")
@DiscriminatorValue("M")
public class Movie extends Item{

    private String director;
    private String actor;

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }


    @Override
    public String getAuthor() {
        return director;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
