package me.oopty.chapter8;

import me.oopty.chapter8.Member;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void testMember() {

        doTransaction(em -> {
            Member member = new Member(1L, "oopty", 10);
            em.persist(member);
        });

        doTransaction(em -> {
            System.out.println("before: em.find");
            Member member = em.find(Member.class, 1L);
            System.out.println("after: em.find");
            System.out.println("member: " + member);
        });

        doTransaction(em -> {
            System.out.println("before: em.getReference");
            Member member = em.getReference(Member.class, 1L);
            System.out.println("after: em.getReference");
            System.out.println("member: " + member);
        });

        doTransaction(em -> {
            System.out.println("before: em.getReference");
            Member member = em.getReference(Member.class, 1L);
            System.out.println("after: em.getReference");

            Assertions.assertThat(em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(member)).isFalse();
            Hibernate.initialize(member);
            Assertions.assertThat(em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(member)).isTrue();
        });
    }
    @Test
    void testRelation() {

        doTransaction(em -> {
            Member member = new Member(1L, "oopty", 10);
            Team team = new Team(1L, "team1");
            member.setTeam(team);

            em.persist(team);
            em.persist(member);
        });

        doTransaction(em -> {
            Team team = em.find(Team.class, 1L);
            team.getMembers().get(0);
            /*
             select
                members0_.TEAM_ID as team_id4_0_0_,
                members0_.id as id1_0_0_,
                members0_.id as id1_0_1_,
                members0_.age as age2_0_1_,
                members0_.name as name3_0_1_,
                members0_.TEAM_ID as team_id4_0_1_
            from
                Member members0_
            where
                members0_.TEAM_ID=?
             */
        });
    }
    @Test
    void testCaseCade() {

        doTransaction(em -> {
            Parent parent = new Parent(1L, "parent1");
            Child child1 = new Child(1L);
            Child child2 = new Child(2L);

            child1.setParent(parent);
            child2.setParent(parent);
            parent.getChildren().add(child1);
            parent.getChildren().add(child2);

            em.persist(parent);
            em.persist(child1);
            em.persist(child2);
        });

        doTransaction(em -> {
            Parent parent = em.find(Parent.class, 1L);

            Assertions.assertThat(parent).isNotNull();
            Assertions.assertThat(parent.getChildren().size()).isEqualTo(2);
        });

        doTransaction(em -> {
            Parent parent = em.find(Parent.class, 1L);
            parent.getChildren().clear();
        });

        doTransaction(em -> {
            Child child1 = em.find(Child.class, 1L);
            Child child2 = em.find(Child.class, 2L);
            Assertions.assertThat(child1).isNull();
            Assertions.assertThat(child2).isNull();
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
