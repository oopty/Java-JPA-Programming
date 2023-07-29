package me.oopty.chapter6.onetomany;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER_V3")
public class MemberV3 {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
