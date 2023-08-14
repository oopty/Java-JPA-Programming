import me.oopty.chapter14.domain.Member;
import me.oopty.chapter14.domain.Room;
import me.oopty.chapter14.domain.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

public class CollectionTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

    @Test
    void switchCollectionToHibernateCollection() {
        Team team = new Team();
        System.out.println("before: team.getMembers().getClass() " + team.getMembers().getClass());
        System.out.println("before: team.getMembers() " + team.getMembers());

        doTransaction((em) -> {
            em.persist(team);
        });
        System.out.println("after: team.getMembers().getClass() " + team.getMembers().getClass());
    }

    @Test
    void compareListAndSet() {
        Team team = new Team();

        Member member = new Member();
        team.getMembers().add(member);
        member.setTeam(team);


        Room room = new Room();
        team.getRooms().add(room);
        room.setTeam(team);

        doTransaction((em) -> {
            em.persist(team);
            em.persist(room);
            em.persist(member);
//            Hibernate:
//            /* insert me.oopty.chapter14.domain.Team
//             */ insert
//                    into
//            Team
//                    (id)
//            values
//                    (?)
//            Hibernate:
//            /* insert me.oopty.chapter14.domain.Room
//             */ insert
//                    into
//            Room
//                    (team_id, id)
//            values
//                    (?, ?)
//            Hibernate:
//            /* insert me.oopty.chapter14.domain.Member
//             */ insert
//                    into
//            Member
//                    (team_id, id)
//            values
//                    (?, ?)
        });

        doTransaction(em -> {
            em.merge(team);

            Member member1 = new Member();
            team.getMembers().add(member1);
            /* lazy loading
             * select
             *         member0_.id as id1_0_0_,
             *         member0_.team_id as team_id2_0_0_,
             *         team1_.id as id1_2_1_
             *     from
             *         Member member0_
             *     left outer join
             *         Team team1_
             *             on member0_.team_id=team1_.id
             *     where
             *         member0_.id=?
             */
            member1.setTeam(team);


            Room room1 = new Room();
            team.getRooms().add(room1);
            /* lazy loading
             * select
             *         room0_.id as id1_1_0_,
             *         room0_.team_id as team_id2_1_0_,
             *         team1_.id as id1_2_1_
             *     from
             *         Room room0_
             *     left outer join
             *         Team team1_
             *             on room0_.team_id=team1_.id
             *     where
             *         room0_.id=?
             */
            room1.setTeam(team);

            em.persist(member1);
            em.persist(room1);
            /* Set이므로 중복 검사 => 컬렉션 한 번 더 초기화
             * select
                    rooms0_.team_id as team_id2_1_0_,
                    rooms0_.id as id1_1_0_,
                    rooms0_.id as id1_1_1_,
                    rooms0_.team_id as team_id2_1_1_
                from
                    Room rooms0_
                where
                    rooms0_.team_id=?
             */
        });
    }

    @Test
    void testOrderByList() {
        doTransaction(em -> {
            Team team = new Team();
            Member member1 = new Member("name2");
            Member member2 = new Member("name1");

            team.getMembers().add(member1);
            team.getMembers().add(member2);
            member1.setTeam(team);
            member2.setTeam(team);

            em.persist(team);
            em.persist(member1);
            em.persist(member2);

            assertThat(team.getMembers().get(0).getName()).isEqualTo("name2");
            assertThat(team.getMembers().get(1).getName()).isEqualTo("name1");
        });

        doTransaction(em -> {
            Team team = em.find(Team.class, 1L);
            List<Member> members = team.getMembers();

            // 이름으로 정렬된 순서로 조회됨
            assertThat(members.get(0).getName()).isEqualTo("name1");
            assertThat(members.get(1).getName()).isEqualTo("name2");
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
