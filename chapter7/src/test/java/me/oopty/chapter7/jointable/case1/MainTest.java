package me.oopty.chapter7.jointable.case1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void testIdClass() {
        /*
         1. create table CHILD6 (
               CHILD_ID varchar(255) not null,
                name varchar(255),
                primary key (CHILD_ID)
            )

         2. create table PARENT6s (
               PARENT_ID varchar(255) not null,
                name varchar(255),
                primary key (PARENT_ID)
            )

         3. create table PARENT_CHILD (
               PARENT_ID varchar(255),
                CHILD_ID varchar(255) not null,
                primary key (CHILD_ID)
            )
         */

        doTransaction(em -> {
            Parent parent = new Parent();
            parent.setParentId("p1");
            parent.setName("oopty1");

            Child child = new Child();
            child.setId("c1");
            child.setName("oopty2");
            child.setParent(parent);

            em.persist(parent);
            em.persist(child);
        });

        doTransaction(em -> {
            Child child = em.find(Child.class, "c1");
            assertThat(child.getParent().getName()).isEqualTo("oopty1");
        });
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
