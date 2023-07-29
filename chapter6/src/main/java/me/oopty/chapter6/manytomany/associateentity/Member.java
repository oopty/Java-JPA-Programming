package me.oopty.chapter6.manytomany.associateentity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "MemberV7")
@Table(name = "MEMBER_V7")
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;

    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProduct = new ArrayList<>();

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

    public List<MemberProduct> getMemberProduct() {
        return memberProduct;
    }

    public void setMemberProduct(List<MemberProduct> memberProduct) {
        this.memberProduct = memberProduct;
    }
}
