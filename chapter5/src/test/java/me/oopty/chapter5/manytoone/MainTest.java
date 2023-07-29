package me.oopty.chapter5.manytoone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.function.Consumer;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void saveManyToOne_case1() {
        doTransaction(em -> {
            Team team = new Team();
            team.setId(1L);
            team.setName("team1");

            Member member1 = new Member();
            member1.setId(1L);
            member1.setUsername("oopty");
            member1.setTeam(team);

            Member member2 = new Member();
            member2.setId(2L);
            member2.setUsername("oopty");
            member2.setTeam(team);

            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });

        /*
        1. insert into TEAM (name, TEAM_ID) values (?, ?)
        2. insert into MEMBER (TEAM_ID, username, MEMBER_ID) values (?, ?, ?)
        3. insert into MEMBER (TEAM_ID, username, MEMBER_ID) values (?, ?, ?)
         */
    }
    @Test
    void saveManyToOne_case2() {
        doTransaction(em -> {
            Team team = new Team();
            team.setId(1L);
            team.setName("team1");

            Member member1 = new Member();
            member1.setId(1L);
            member1.setUsername("oopty");
            member1.setTeam(team);

            Member member2 = new Member();
            member2.setId(2L);
            member2.setUsername("oopty");
            member2.setTeam(team);

            em.persist(member1);
            em.persist(member2);
            em.persist(team);
        });

        /*
        1. insert into MEMBER (TEAM_ID, username, MEMBER_ID) values (?, ?, ?)
        2. insert into MEMBER (TEAM_ID, username, MEMBER_ID) values (?, ?, ?)
        3. insert into TEAM (name, TEAM_ID) values (?, ?)
        4. update MEMBER set TEAM_ID=?, username=? where MEMBER_ID=?
        5. update MEMBER set TEAM_ID=?, username=? where MEMBER_ID=?
         */
    }
    @Test
    void findManyToOne_case1() {
        doTransaction(em -> {
            Team team = new Team();
            team.setId(1L);
            team.setName("team1");

            Member member1 = new Member();
            member1.setId(1L);
            member1.setUsername("oopty");
            member1.setTeam(team);

            Member member2 = new Member();
            member2.setId(2L);
            member2.setUsername("oopty");
            member2.setTeam(team);

            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });


        doTransaction(em -> {
            Member member = em.find(Member.class, 1L);
            System.out.println(member);
        });
        /*
        select
            member0_.MEMBER_ID as member_i1_0_0_,
            member0_.TEAM_ID as team_id3_0_0_,
            member0_.username as username2_0_0_,
            team1_.TEAM_ID as team_id1_1_1_,
            team1_.name as name2_1_1_
        from
            MEMBER member0_
        left outer join
            TEAM team1_
                on member0_.TEAM_ID=team1_.TEAM_ID
        where
            member0_.MEMBER_ID=?
         */
    }
    @Test
    void findManyToOne_case2() {
        doTransaction(em -> {
            Team team = new Team();
            team.setId(1L);
            team.setName("team1");

            Member member1 = new Member();
            member1.setId(1L);
            member1.setUsername("oopty");
            member1.setTeam(team);

            Member member2 = new Member();
            member2.setId(2L);
            member2.setUsername("oopty");
            member2.setTeam(team);

            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });


        doTransaction(em -> {
            Member member = em.createQuery("select m from Member m where id= :id", Member.class)
                    .setParameter("id", 1L)
                    .getSingleResult();

            System.out.println(member);
        });
        /*
        1.  select
                member0_.MEMBER_ID as member_i1_0_,
                member0_.TEAM_ID as team_id3_0_,
                member0_.username as username2_0_
            from
                MEMBER member0_
            where
                member0_.MEMBER_ID=?

         2. select
                team0_.TEAM_ID as team_id1_1_0_,
                team0_.name as name2_1_0_
            from
                TEAM team0_
            where
                team0_.TEAM_ID=?
         */
    }

    @Test
    void updateManyToOne() {
        doTransaction(em -> {
            Team team1 = new Team();
            team1.setId(1L);
            team1.setName("team1");

            Team team2 = new Team();
            team2.setId(2L);
            team2.setName("team2");

            Member member = new Member();
            member.setId(1L);
            member.setUsername("oopty");
            member.setTeam(team1);

            em.persist(team1);
            em.persist(member);
        });

        doTransaction(em -> {
            Team team2 = em.find(Team.class, 2L);
            Member member = em.find(Member.class, 1L);

            member.setTeam(team2);
        });

        /*
         1. select
                team0_.TEAM_ID as team_id1_1_0_,
                team0_.name as name2_1_0_
            from
                TEAM team0_
            where
                team0_.TEAM_ID=?

         2. select
                member0_.MEMBER_ID as member_i1_0_0_,
                member0_.TEAM_ID as team_id3_0_0_,
                member0_.username as username2_0_0_,
                team1_.TEAM_ID as team_id1_1_1_,
                team1_.name as name2_1_1_
            from
                MEMBER member0_
            left outer join
                TEAM team1_
                    on member0_.TEAM_ID=team1_.TEAM_ID
            where
                member0_.MEMBER_ID=?

         3. update
                MEMBER
            set
                TEAM_ID=?,
                username=?
            where
                MEMBER_ID=?
         */
    }

    @Test
    void deleteManyToOne() {
        doTransaction(em -> {
            Team team = new Team();
            team.setId(1L);
            team.setName("team");

            Member member = new Member();
            member.setId(1L);
            member.setUsername("oopty");
            member.setTeam(team);

            em.persist(team);
            em.persist(member);
        });

        doTransaction(em -> {
            Member member = em.find(Member.class, 1L);
            member.setTeam(null);
        });

        /*
         1. select
                member0_.MEMBER_ID as member_i1_0_0_,
                member0_.TEAM_ID as team_id3_0_0_,
                member0_.username as username2_0_0_,
                team1_.TEAM_ID as team_id1_1_1_,
                team1_.name as name2_1_1_
            from
                MEMBER member0_
            left outer join
                TEAM team1_
                    on member0_.TEAM_ID=team1_.TEAM_ID
            where
                member0_.MEMBER_ID=?
         2. update
                MEMBER
            set
                TEAM_ID=?,
                username=?
            where
                MEMBER_ID=?
         */
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
