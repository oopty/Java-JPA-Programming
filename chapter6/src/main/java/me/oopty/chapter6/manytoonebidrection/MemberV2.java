package me.oopty.chapter6.manytoonebidrection;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER_V2")
public class MemberV2 {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private TeamV2 team;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public TeamV2 getTeam() {
        return team;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTeam(TeamV2 team) {
        if(this.team != null)
            this.team.memberV2s.remove(this);

        this.team = team;

        if(!team.getMembers().contains(this))
            team.addMember(this);
    }
}
