package me.oopty.chapter7.mappedsuperclass;

import javax.persistence.Entity;

@Entity
public class Member extends BaseEntity{

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
