package me.oopty.chapter10;

import me.oopty.chapter10.code.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class JPQLTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void testTypeQuery() {
        doTransaction(em -> {
            Member member = new Member("oopty", 10);
            em.persist(member);
        });
        doTransaction(em -> {
            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
            List<Member> resultList = query.getResultList();
            Assertions.assertThat(resultList.size()).isEqualTo(1);
            Assertions.assertThat(resultList.get(0).getUsername()).isEqualTo("oopty");
        });
    }
    @Test
    void testQuery() {
        doTransaction(em -> {
            Member member = new Member("oopty", 10);
            em.persist(member);
        });
        doTransaction(em -> {
            Query query = em.createQuery("select m.username, m.age from Member m");
            List<Object> resultList = query.getResultList();
            Assertions.assertThat(resultList.size()).isEqualTo(1);
            Object[] o = (Object[]) resultList.get(0);
            Assertions.assertThat(o[0]).isEqualTo("oopty");
            Assertions.assertThat(o[1]).isEqualTo(10);
        });
    }

    @Test
    void testQueryEntity() {
        doTransaction(em -> {
            Member member = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member.setTeam(team);
            team.getMember().add(member);
            em.persist(team);
            em.persist(member);
        });

        doTransaction(em -> {
            TypedQuery<Team> query = em.createQuery("select m.team from Member m", Team.class);
            Team team = query.getSingleResult();
            Assertions.assertThat(team.getName()).isEqualTo("team1");

        });
    }

    @Test
    void testNewKeyword() {
        doTransaction(em -> {
            Member member = new Member("oopty", 10);
            em.persist(member);
        });

        doTransaction(em -> {
            TypedQuery<UserDTO> query = em.createQuery("select new me.oopty.chapter10.code.UserDTO(m.username, m.age) from Member m", UserDTO.class);
            UserDTO userDTO = query.getSingleResult();
            Assertions.assertThat(userDTO.getUsername()).isEqualTo("oopty");
            Assertions.assertThat(userDTO.getAge()).isEqualTo(10);

        });
    }

    @Test
    void testPagination() {
        doTransaction(em -> {
            Member member = new Member("oopty", 10);
            em.persist(member);
        });

        doTransaction(em -> {
            TypedQuery<Member> query = em.createQuery("select m from Member m order by m.age desc", Member.class);

            query.setMaxResults(10);
            query.setFirstResult(0);
            // 1번째 부터 10개씩 조회
        });
    }

    @Test
    void testAggregate() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            em.persist(member1);
            Member member2 = new Member("oopty", 20);
            em.persist(member2);
            Member member3 = new Member("oopty", 30);
            em.persist(member3);
            Member member4 = new Member("oopty", 40);
            em.persist(member4);
        });

        doTransaction(em -> {
            Query query = em.createQuery("select avg(m.age), sum(m.age), min(m.age), max(m.age), count(*) from Member m");
            Object[] singleResult = (Object[]) query.getSingleResult();
            Assertions.assertThat(singleResult[0]).isEqualTo(25.0);
            Assertions.assertThat(singleResult[1]).isEqualTo(100L);
            Assertions.assertThat(singleResult[2]).isEqualTo(10);
            Assertions.assertThat(singleResult[3]).isEqualTo(40);
            Assertions.assertThat(singleResult[4]).isEqualTo(4L);
        });
    }

    @Test
    void testInnerJoin() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });

        doTransaction(em -> {
            Query query = em.createQuery("select m, m.team from Member m join m.team");
            List<Object[]> resultList = query.getResultList();

            Assertions.assertThat(resultList.size()).isEqualTo(2);
            Object[] result = resultList.get(0);
            Member member = (Member) result[0];
            Team team = (Team) result[1];

            Assertions.assertThat(member.getUsername()).isEqualTo("oopty");
            Assertions.assertThat(team.getName()).isEqualTo("team1");
        });
    }

    @Test
    void testOuterJoin() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Member member3 = new Member("ytpoo", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            member3.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            team.getMember().add(member3);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
        });

        doTransaction(em -> {
            Query query = em.createQuery("select m, m.team from Member m left join m.team");
            List<Object[]> resultList = query.getResultList();

            Assertions.assertThat(resultList.size()).isEqualTo(3);
            Object[] result = resultList.get(0);
            Member member = (Member) result[0];
            Team team = (Team) result[1];

            Assertions.assertThat(member.getUsername()).isEqualTo("oopty");
            Assertions.assertThat(team.getName()).isEqualTo("team1");
        });
    }

    @Test
    void testThetaJoin() {
        doTransaction(em -> {
            Member member1 = new Member("team1", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            team.getMember().add(member1);
            em.persist(team);
            em.persist(member1);
        });

        doTransaction(em -> {
            Query query = em.createQuery("select m, m.team from Member m join m.team where m.username = m.team.name");
            List<Object[]> resultList = query.getResultList();

            Assertions.assertThat(resultList.size()).isEqualTo(1);
            Object[] result = resultList.get(0);
            Member member = (Member) result[0];
            Team team = (Team) result[1];

            Assertions.assertThat(member.getUsername()).isEqualTo("team1");
            Assertions.assertThat(team.getName()).isEqualTo("team1");

            /*
             1. select
                    member0_.id as id1_0_0_,
                    team1_.id as id1_3_1_,
                    member0_.age as age2_0_0_,
                    member0_.TEAM_ID as team_id4_0_0_,
                    member0_.username as username3_0_0_,
                    team1_.name as name2_3_1_
                from
                    Member member0_
                inner join
                    Team team1_
                        on member0_.TEAM_ID=team1_.id
                where
                    member0_.username=team1_.name
             */
        });
    }

    @Test
    void testJoinOn() {
        doTransaction(em -> {
            Member member1 = new Member("team1", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            team.getMember().add(member1);
            em.persist(team);
            em.persist(member1);
        });

        doTransaction(em -> {
            Query query = em.createQuery("select m, m.team from Member m join m.team on m.username = m.team.name");
            List<Object[]> resultList = query.getResultList();

            Assertions.assertThat(resultList.size()).isEqualTo(1);
            Object[] result = resultList.get(0);
            Member member = (Member) result[0];
            Team team = (Team) result[1];

            Assertions.assertThat(member.getUsername()).isEqualTo("team1");
            Assertions.assertThat(team.getName()).isEqualTo("team1");

            /*
             select
                member0_.id as id1_0_0_,
                team1_.id as id1_3_1_,
                member0_.age as age2_0_0_,
                member0_.TEAM_ID as team_id4_0_0_,
                member0_.username as username3_0_0_,
                team1_.name as name2_3_1_
            from
                Member member0_
            inner join
                Team team1_
                    on member0_.TEAM_ID=team1_.id
                    and (
                        member0_.username=team1_.name
                    )
             */
        });
    }

    @Test
    void testFetchJoin() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });

        doTransaction(em -> {
            TypedQuery<Team> query = em.createQuery("select t from Team t join fetch t.member", Team.class);
            TypedQuery<Team> queryWithDistinct = em.createQuery("select distinct t from Team t join fetch t.member", Team.class);
            List<Team> resultList = query.getResultList();
            Team singleResult = queryWithDistinct.getSingleResult();

            Assertions.assertThat(resultList.size()).isEqualTo(2);
            Assertions.assertThat(resultList.get(0)).isEqualTo(resultList.get(1)); // 두 변수의 참조값이 같음
            Assertions.assertThat(singleResult.getMember().get(0).getUsername()).isEqualTo("oopty"); // not lazy loading
        });
    }

    @Test
    void testImplicitJoin() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });

        doTransaction(em -> {
            TypedQuery<Team> query = em.createQuery("select m.team from Member m", Team.class);
            Team singleResult = query.getSingleResult();

            Assertions.assertThat(singleResult.getName()).isEqualTo("team1"); // 두 변수의 참조값이 같음
        });
    }

    @Test
    void testNamedQuery() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });

        doTransaction(em -> {
            TypedQuery<Member> query = em.createNamedQuery("Member.findByUsername", Member.class);
            query.setParameter("username", "oopty");
            List<Member> resultList = query.getResultList();
            Assertions.assertThat(resultList.size()).isEqualTo(2);
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
