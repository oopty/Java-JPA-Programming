package me.oopty.chapter8.case1;

import javax.persistence.*;

@Entity(name = "CHILD6")
@Table(name = "CHILD6")
public class Child {

    @Id
    @Column(name = "CHILD_ID")
    private String id;

    @ManyToOne
    @JoinTable(name="PARENT_CHILD",
            joinColumns = @JoinColumn(name = "CHILD_ID"),
            inverseJoinColumns = @JoinColumn(name = "PARENT_ID"))
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
