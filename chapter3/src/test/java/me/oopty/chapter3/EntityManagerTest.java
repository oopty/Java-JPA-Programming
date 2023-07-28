package me.oopty.chapter3;

import org.assertj.core.api.Assertions;
import org.hibernate.annotations.DynamicUpdate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

public class EntityManagerTest {

    private EntityManager em;
    private EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void persist_success() {
        Member member = new Member();
        member.setId("id1");
        member.setUsername("oopty");
        member.setAge(10);

        em.persist(member);
    }

    @Test
    public void persist_fail() {
        Member member1 = new Member();
        member1.setId("id1");
        member1.setUsername("oopty");
        member1.setAge(10);

        Member member2 = new Member();
        member2.setId("id1");
        member2.setUsername("oopty");
        member2.setAge(10);

        em.persist(member1);
        assertThatThrownBy(() -> em.persist(member2))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void update() {
        doTransaction(em, () -> {
            Member member = new Member();
            member.setId("id1");
            member.setUsername("oopty");
            member.setAge(10);

            em.persist(member);
            member.setUsername("typoo");
        });

        Member singleResult = em.createQuery("select m from Member m where m.id = :id", Member.class)
                .setParameter("id", "id1")
                .getSingleResult();

        assertThat(singleResult.getUsername()).isEqualTo("typoo");
        /* update MEMBER set age=?, NAME=? where ID=? */
    }

    @Test
    void update_dynamic () {
        doTransaction(em, () -> {
            MemberDynamic member = new MemberDynamic();
            member.setId("id1");
            member.setUsername("oopty");
            member.setAge(10);

            em.persist(member);
            member.setUsername("typoo");
        });

        MemberDynamic singleResult = em.createQuery("select m from MemberDynamic m where m.id = :id", MemberDynamic.class)
                .setParameter("id", "id1")
                .getSingleResult();

        assertThat(singleResult.getUsername()).isEqualTo("typoo");
        /*update MEMBER_DYNAMIC set NAME=? where ID=?*/
    }

    @Test
    void fluxMode_auto() {
        em.setFlushMode(FlushModeType.AUTO); // default
        doTransaction(em, () -> {
            Member member = new Member();
            member.setId("id1");
            member.setUsername("oopty");
            member.setAge(10);

            em.persist(member);

            em.createQuery("select m from Member m where m.id = :id", Member.class)
                    .setParameter("id", "id1")
                    .getSingleResult();

            member.setUsername("typoo");
        });

        /* Query Order
         * 1. insert into MEMBER (age, NAME, ID) values (?, ?, ?)
         *
         * 2. select
                member0_.ID as id1_0_,
                member0_.age as age2_0_,
                member0_.NAME as name3_0_
           from
                MEMBER member0_
           where
                member0_.ID=?
         *
         * 3. update MEMBER set age=?, NAME=? where ID=?
         */
    }

    @Test
    void fluxMode_commit() {
        em.setFlushMode(FlushModeType.COMMIT);
        doTransaction(em, () -> {
            Member member = new Member();
            member.setId("id1");
            member.setUsername("oopty");
            member.setAge(10);

            em.persist(member);

            em.createQuery("select m from Member m", Member.class).getResultList();

            member.setUsername("typoo");
        });


        /* Query Order
         * 1. select
                member0_.ID as id1_0_,
                member0_.age as age2_0_,
                member0_.NAME as name3_0_
           from
                MEMBER member0_
           where
                member0_.ID=?
         *
         * 2. insert into MEMBER (age, NAME, ID) values (?, ?, ?)
         *
         * 3. update MEMBER set age=?, NAME=? where ID=?
         */
    }

    @Test
    void detach_merge_test() {
        Member member = new Member();
        member.setId("id1");
        member.setUsername("oopty");
        member.setAge(10);

        em.persist(member);
        assertThat(em.contains(member)).isTrue();

        em.detach(member);
        assertThat(em.contains(member)).isFalse();

        Member merged = em.merge(member);
        assertThat(em.contains(member)).isFalse();
        assertThat(em.contains(merged)).isTrue();

        Member member2 = new Member();
        member2.setId("id2");
        member2.setUsername("oopty");
        member2.setAge(10);

        Member merged2 = em.merge(member2);
        assertThat(em.contains(member2)).isFalse();
        assertThat(em.contains(merged2)).isTrue();
    }

    private void doTransaction(EntityManager em, Runnable logic) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            logic.run();
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
            throw e;
        }
    }

}
