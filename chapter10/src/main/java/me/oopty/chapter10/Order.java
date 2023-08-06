package me.oopty.chapter10;

import javax.persistence.*;

@Entity
@Table(name = "ORDERS")
public class Order {
    @Id @GeneratedValue
    private Long id;

    private int orderAmount;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Embedded
    private Address address;

    @OneToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    public Order() {
    }

    public Order(int orderAmount, Member member, Address address, Product product) {
        this.orderAmount = orderAmount;
        this.member = member;
        this.address = address;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
