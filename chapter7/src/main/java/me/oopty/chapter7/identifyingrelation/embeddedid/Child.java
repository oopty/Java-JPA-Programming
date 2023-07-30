package me.oopty.chapter7.identifyingrelation.embeddedid;

import javax.persistence.*;

@Entity(name = "CHILD2")
@Table(name = "CHILD2")
public class Child {

    @Id
    @Column(name = "CHILD_ID")
    private String id;


    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),
            @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")}
    )
    private Parent parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
