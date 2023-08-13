package me.oopty.chapter13.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Sample {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "sample")
    private List<Tample> tamples;

    public Sample() {
    }

    public Sample(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Sample(String name) {
        this.name = name;
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

    public List<Tample> getTamples() {
        return tamples;
    }

    public void setTamples(List<Tample> tamples) {
        this.tamples = tamples;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
