package me.oopty.chapter7.identifyingrelation.embeddedid;

import javax.persistence.*;

@Entity(name = "PARENT2")
@Table(name = "PARENT2")
public class Parent {
    @EmbeddedId
    ParentId parentId;

    private String name;


    public ParentId getParentId() {
        return parentId;
    }

    public void setParentId(ParentId parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
