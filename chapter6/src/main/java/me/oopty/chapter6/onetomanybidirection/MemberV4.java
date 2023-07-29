package me.oopty.chapter6.onetomanybidirection;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER_V4")
public class MemberV4 {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;


    @ManyToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    private TeamV4 Team;

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
