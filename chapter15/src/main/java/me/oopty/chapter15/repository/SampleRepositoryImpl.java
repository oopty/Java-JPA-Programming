package me.oopty.chapter15.repository;

import me.oopty.chapter15.domain.Sample;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static me.oopty.chapter15.domain.QSample.*;

public class SampleRepositoryImpl extends QueryDslRepositorySupport implements CustomSampleRepository {
    @PersistenceContext
    private EntityManager em;

    public SampleRepositoryImpl() {
        super(Sample.class);
    }

    @Override
    public Sample findByName2(String name) {
        return em.createQuery("select s from Sample s where s.name = :name", Sample.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    public Sample contains(String name) {
        return from(sample)
                .where(sample.name.contains(name))
                .singleResult(sample);
    }
}
