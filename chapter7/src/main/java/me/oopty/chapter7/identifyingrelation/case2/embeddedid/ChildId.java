package me.oopty.chapter7.identifyingrelation.case2.embeddedid;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChildId implements Serializable {

    @Column(name = "CHILD_ID")
    private String id;

    @Column(name = "PARENT_ID")
    private String parent;

    public ChildId() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildId childId = (ChildId) o;
        return Objects.equals(id, childId.id) && Objects.equals(parent, childId.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parent);
    }
}
