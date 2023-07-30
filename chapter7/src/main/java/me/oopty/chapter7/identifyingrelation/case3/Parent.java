package me.oopty.chapter7.identifyingrelation.case3;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "PARENT5")
@Table(name = "PARENT5")
public class Parent {
    @Id
    @Column(name = "PARENT_ID")
    private String parentId;

    private String name;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
