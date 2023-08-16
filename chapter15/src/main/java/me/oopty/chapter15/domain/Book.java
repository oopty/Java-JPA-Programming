package me.oopty.chapter15.domain;

import me.oopty.chapter15.visitor.Visitor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "BOOK")
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "BOOK_ID")
public class Book extends Item {

    private String author;

    private String isbn;

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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
