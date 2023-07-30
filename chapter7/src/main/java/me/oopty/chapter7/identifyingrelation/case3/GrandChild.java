package me.oopty.chapter7.identifyingrelation.case3;

import javax.persistence.*;

@Entity(name = "GRAND_CHILD3")
@Table(name = "GRAND_CHILD3")
public class GrandChild {

    @Id @Column(name= "GRAND_CHILD_ID")
    private String id;

    @ManyToOne
    @JoinColumn(name = "CHILD_ID")
    private Child child;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }
}
