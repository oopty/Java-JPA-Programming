package me.oopty.chapter9;

import javax.persistence.*;
import java.util.*;

@Entity
public class Member {

    @Id
    private Long id;
    private String name;

    @Embedded Address homeAddress;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "COMPANY_CITY")),
            @AttributeOverride(name = "street", column = @Column(name = "COMPANY_STREET")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "COMPANY_ZIPCODE"))
    })
    Address companyAddress;

    @ElementCollection
    @CollectionTable(
            name = "FAVORITE_FOODS",
            joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name = "FOOD_NAME")
    private List<String> favoriteFoods = new LinkedList<>();

    public Member() {
    }

    public Member(Long id, String name, Address homeAddress, Address companyAddress) {
        this.id = id;
        this.name = name;
        this.homeAddress = homeAddress;
        this.companyAddress = companyAddress;
    }

    public List<String> getFavoriteFoods() {
        return favoriteFoods;
    }

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

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Address getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(Address companyAddress) {
        this.companyAddress = companyAddress;
    }
}
