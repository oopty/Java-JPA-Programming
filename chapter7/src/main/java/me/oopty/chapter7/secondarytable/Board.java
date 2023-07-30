package me.oopty.chapter7.secondarytable;

import javax.persistence.*;

@Entity(name = "BOARD2")
@Table(name = "BOARD2")
@SecondaryTable(name = "BOARD_DETAIL2", pkJoinColumns = @PrimaryKeyJoinColumn(name = "BOARD_DETAIL_ID"))
public class Board {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Column(table="BOARD_DETAIL2")
    private String content;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
