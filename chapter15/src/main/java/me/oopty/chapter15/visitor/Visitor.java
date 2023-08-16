package me.oopty.chapter15.visitor;

import me.oopty.chapter15.domain.Album;
import me.oopty.chapter15.domain.Book;
import me.oopty.chapter15.domain.Movie;

public interface Visitor {

    void visit(Book book);
    void visit(Album album);
    void visit(Movie movie);
}
