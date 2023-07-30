package me.oopty.chapter7.inheritance.v1;

import javax.persistence.*;

@Entity
@Table(name = "ALBUM")
@DiscriminatorValue("A")
public class Album extends Item{
    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
