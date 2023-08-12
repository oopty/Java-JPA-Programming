package me.oopty.chapter11.repository;

import me.oopty.chapter11.domain.Sample;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class SampleRepository {
    @PersistenceContext
    private EntityManager em;

    public Sample findOne(Long sampleId) {
        return em.find(Sample.class, sampleId);
    }

    public Sample save(Sample sample) {
        if(sample.getId() != null) {
            sample = em.merge(sample);
        } else {
            em.persist(sample);
        }
        return sample;
    }

    public Sample delete(Long sampleId) {
        Sample sample = em.find(Sample.class, sampleId);
        em.remove(sample);
        return sample;
    }
}
