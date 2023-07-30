package me.oopty.chapter7.identifyingrelation.case1.idclass;

import java.io.Serializable;
import java.util.Objects;

public class ParentId implements Serializable {

    private String parentId1;
    private String parentId2;

    public String getParentId1() {
        return parentId1;
    }

    public void setParentId1(String parentId1) {
        this.parentId1 = parentId1;
    }

    public String getParentId2() {
        return parentId2;
    }

    public void setParentId2(String parentId2) {
        this.parentId2 = parentId2;
    }

    public ParentId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentId parentId = (ParentId) o;
        return Objects.equals(parentId1, parentId.parentId1) && Objects.equals(parentId2, parentId.parentId2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId1, parentId2);
    }
}
