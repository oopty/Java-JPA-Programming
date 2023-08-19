package me.oopty.chapter16;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board {
    @Id @GeneratedValue
    private Long id;

    private String title;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "board")
    private List<Attachement> attchments = new ArrayList<>();


    public Board() {
    }

    public Board(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", version=" + version +
                '}';
    }

    public List<Attachement> getAttchments() {
        return attchments;
    }
}
