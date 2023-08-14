package me.oopty.chapter14.domain;

import me.oopty.chapter14.TeamListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(TeamListener.class)
public class Team{
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "team")
    @OrderBy("name asc")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private Set<Room> rooms = new HashSet<>();

    public Long getId() {
        return id;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Set<Room> getRooms() {
        return rooms;
    }
}
