package me.oopty.chapter5.manytoonebidirection;

import me.oopty.chapter5.manytoone.Member;
import me.oopty.chapter5.manytoone.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    private void doTransaction(Consumer<EntityManager> logic) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            logic.accept(em);
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
