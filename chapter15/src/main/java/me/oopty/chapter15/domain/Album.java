package me.oopty.chapter15.domain;

import me.oopty.chapter15.visitor.Visitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ALBUM")
@DiscriminatorValue("A")
public class Album extends Item {
    private String author;

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
