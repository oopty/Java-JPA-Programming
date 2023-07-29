package me.oopty.chapter6.onetomanybidirection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TEAM_V4")
public class TeamV4 {

    @Id
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany
    @JoinColumn(name = "TEAM_ID")
    List<MemberV4> members = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MemberV4> getMembers() {
        return members;
    }

    public void addMember(MemberV4 member) {
        members.add(member);
    }
}
