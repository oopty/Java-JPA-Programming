package me.oopty.chapter14;

import javax.persistence.*;

public class TeamListener {
    @PrePersist
    public void perPersist(Object obj) {
        System.out.println("persist obj = " + obj);
    }

    @PostPersist
    public void postPersist(Object obj) {
        System.out.println("postPersist obj = " + obj);
    }

    @PostLoad
    public void postLoad(Object obj) {
        System.out.println("postLoad obj = " + obj);
    }

    @PreRemove
    public void preRemove(Object obj) {
        System.out.println("preRemove obj = " + obj);
    }

    @PostRemove
    public void postRemove(Object obj) {
        System.out.println("postRemove obj = " + obj);
    }
}
