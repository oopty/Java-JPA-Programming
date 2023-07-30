package me.oopty.chapter7.identifyingrelation.case2.idclass;

import javax.persistence.*;

@Entity(name = "CHILD3")
@Table(name = "CHILD3")
@IdClass(ChildId.class)
public class Child {

    @Id
    @Column(name = "CHILD_ID")
    private String id;

    @Id
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Parent parent;

    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
