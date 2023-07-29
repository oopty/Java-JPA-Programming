package me.oopty.chapter6.manytomany;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT")
public class Product {

    @Id
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "products")
    private List<MemberV6> members = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MemberV6> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMember(MemberV6 member) {
        members.add(member);
        if(!member.getProducts().contains(this))
            member.addProduct(this);

    }
}
