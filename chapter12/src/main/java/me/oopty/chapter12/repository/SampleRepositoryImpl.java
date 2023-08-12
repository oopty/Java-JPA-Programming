package me.oopty.chapter12.repository;

import me.oopty.chapter12.domain.Sample;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SampleRepositoryImpl implements CustomSampleRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Sample findByName2(String name) {
        return em.createQuery("select s from Sample s where s.name = :name", Sample.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
