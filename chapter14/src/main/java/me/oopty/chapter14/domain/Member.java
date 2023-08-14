package me.oopty.chapter14.domain;

import me.oopty.chapter14.BooleanToYNConverter;

import javax.persistence.*;

@Entity
@NamedEntityGraph(
        name = "Member.withTeam",
        attributeNodes = @NamedAttributeNode("team"))
public class Member {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Convert(converter = BooleanToYNConverter.class)
    private Boolean vip = false;

    public Member() {
    }

    public Member(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }

    @PrePersist
    public void perPersist() {
        System.out.println("persist id = " + id);
    }

    @PostPersist
    public void postPersist() {
        System.out.println("postPersist id = " + id);
    }

    @PostLoad
    public void postLoad() {
        System.out.println("postLoad id = " + id);
    }

    @PreRemove
    public void preRemove() {
        System.out.println("preRemove id = " + id);
    }

    @PostRemove
    public void postRemove() {
        System.out.println("postRemove id = " + id);
    }
}
