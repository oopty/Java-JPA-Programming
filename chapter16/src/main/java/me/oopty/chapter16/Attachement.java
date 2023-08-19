package me.oopty.chapter16;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Attachement {

    @Id @GeneratedValue
    private Long id;
    private String filename;

    private String path;

    @ManyToOne
    private Board board;

    public Long getId() {
        return id;
    }

    public Attachement() {
    }

    public Attachement(String filename, String path) {
        this.filename = filename;
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
