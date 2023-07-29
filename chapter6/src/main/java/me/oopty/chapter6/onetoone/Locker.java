package me.oopty.chapter6.onetoone;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "LOCKER")
public class Locker {
    @Id
    @Column(name = "LOCKER_ID")
    private Long id;

    @OneToOne(mappedBy = "locker")
    private MemberV5 member;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMember(MemberV5 member) {
        if(this.member != null)
            this.member.setLocker(null);

        this.member = member;
        if(member != null && member.getLocker().orElse(null) != this)
            member.setLocker(this);
    }

    public Optional<MemberV5> getMember() {
        return Optional.ofNullable(this.member);
    }
}
