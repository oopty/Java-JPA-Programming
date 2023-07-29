package me.oopty.chapter6.manytomany.associateentity;

import javax.persistence.*;

@Entity(name = "ProductV2")
@Table(name = "PRODUCT_V2")
public class ProductV2 {

    @Id
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

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
}
