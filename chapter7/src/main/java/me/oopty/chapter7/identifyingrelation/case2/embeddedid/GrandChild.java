package me.oopty.chapter7.identifyingrelation.case2.embeddedid;

import javax.persistence.*;

@Entity(name="GRANDCHILD2")
@Table(name="GRANDCHILD2")
public class GrandChild {

    @EmbeddedId
    private GrandChildId id;

    @MapsId("child")
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "PARENT_ID"),
            @JoinColumn(name = "CHILD_ID")
    })
    private Child child;
    private String name;

    public GrandChildId getId() {
        return id;
    }

    public void setId(GrandChildId id) {
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
