package me.oopty.chapter6.manytoonebidrection;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TEAM_V2")
public class TeamV2 {

    @Id
    @Column(name = "TEAM_ID")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    List<MemberV2> memberV2s = new ArrayList<>();

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

    public List<MemberV2> getMembers() {
        return memberV2s;
    }

    public void addMember(MemberV2 member) {
        this.memberV2s.add(member);

        if(member.getTeam() != this)
            member.setTeam(this);
    }
}
