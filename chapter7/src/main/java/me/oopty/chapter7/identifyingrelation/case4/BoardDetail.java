package me.oopty.chapter7.identifyingrelation.case4;

import javax.persistence.*;

@Entity
public class BoardDetail {
    @Id
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    private String content;


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
