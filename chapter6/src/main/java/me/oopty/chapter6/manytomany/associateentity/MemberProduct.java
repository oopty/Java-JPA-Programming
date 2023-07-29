package me.oopty.chapter6.manytomany.associateentity;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER_PRODUCT")
@IdClass(MemberProductId.class)
public class MemberProduct {
    @Id
    @JoinColumn(name = "MEMBER_ID")
    @ManyToOne
    private Member member;


    @Id
    @JoinColumn(name = "PRODUCT_ID")
    @ManyToOne
    private ProductV2 product;


    private int orderAmount;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public ProductV2 getProduct() {
        return product;
    }

    public void setProduct(ProductV2 product) {
        this.product = product;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }
}
