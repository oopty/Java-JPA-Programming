package me.oopty.chapter6.onetoone;

import me.oopty.chapter6.onetomanybidirection.TeamV4;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "MEMBER_V5")
public class MemberV5 {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

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

    public void setLocker(Locker locker) {
        if(this.locker != null) {
            this.locker.setMember(null);
        }

        this.locker = locker;
        if(locker != null && locker.getMember().orElse(null) != this)
            locker.setMember(this);
    }

    public Optional<Locker> getLocker() {
        return Optional.ofNullable(this.locker);
    }
}
