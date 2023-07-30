package me.oopty.chapter7.identifyingrelation.case4;

import javax.persistence.*;

@Entity
public class Board {
    @Id @GeneratedValue
    private Long id;

    private String title;

    @OneToOne(mappedBy = "board")
    BoardDetail boardDetail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BoardDetail getBoardDetail() {
        return boardDetail;
    }

    public void setBoardDetail(BoardDetail boardDetail) {
        this.boardDetail = boardDetail;
    }
}
