package me.oopty.chapter7.inheritance.v3;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "ALBUM3")
@Table(name = "ALBUM3")
public class Album extends Item {
    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
