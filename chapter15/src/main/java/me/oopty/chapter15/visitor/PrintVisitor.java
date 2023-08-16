package me.oopty.chapter15.visitor;

import me.oopty.chapter15.domain.Album;
import me.oopty.chapter15.domain.Book;
import me.oopty.chapter15.domain.Movie;

public class PrintVisitor implements Visitor {
    @Override
    public void visit(Book book) {
        System.out.println("book.getAuthor()" + book.getAuthor());
    }

    @Override
    public void visit(Album album) {
        System.out.println(album.getAuthor());
    }

    @Override
    public void visit(Movie movie) {
        System.out.println(movie.getAuthor());

    }
}
