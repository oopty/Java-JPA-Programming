package me.oopty.chapter7.identifyingrelation.case2.embeddedid;

import javax.persistence.*;

@Entity(name = "CHILD4")
@Table(name = "CHILD4")
public class Child {

    @EmbeddedId
    private ChildId childId;

    @MapsId("parent")
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Parent parent;

    private String name;

    public ChildId getChildId() {
        return childId;
    }

    public void setChildId(ChildId childId) {
        this.childId = childId;
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
