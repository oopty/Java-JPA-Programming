package me.oopty.chapter7.inheritance.v2;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "ALBUM2")
@Table(name = "ALBUM2")
@DiscriminatorValue("A")
public class Album extends Item {
    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
