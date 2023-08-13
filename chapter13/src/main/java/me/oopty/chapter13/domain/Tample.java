package me.oopty.chapter13.domain;

import javax.persistence.*;

@Entity
public class Tample {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Sample sample;

    public Tample(String name, Sample sample) {
        this.name = name;
        this.sample = sample;
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

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }


}
