package me.oopty.chapter7.identifyingrelation.case2.embeddedid;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GrandChildId implements Serializable {

    @Column(name = "GRAND_CHILD_ID")
    private String id;
    private ChildId child;

    public GrandChildId() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChildId getChild() {
        return child;
    }

    public void setChild(ChildId child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrandChildId that = (GrandChildId) o;
        return Objects.equals(id, that.id) && Objects.equals(child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, child);
    }
}
