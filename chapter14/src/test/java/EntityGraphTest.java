import me.oopty.chapter14.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class EntityGraphTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

    @Test
    void testNamedEntityGraph() {
        doTransaction(em -> {
            Member member = new Member("oopty");
            em.persist(member);
        });

        doTransaction(em -> {
            EntityGraph<?> entityGraph = em.getEntityGraph("Member.withTeam");

            Map hints = new HashMap();
            hints.put("javax.persistence.fetchgraph", entityGraph);

            Member member = em.find(Member.class, 1L, hints);

            /*
             select
                member0_.id as id1_0_0_,
                member0_.name as name2_0_0_,
                member0_.team_id as team_id4_0_0_,
                member0_.vip as vip3_0_0_,
                team1_.id as id1_2_1_
            from
                Member member0_
            left outer join
                Team team1_
                    on member0_.team_id=team1_.id
            where
                member0_.id=?
             */
            assertNotNull(member);
        });

        doTransaction(em -> {
            Member member = em.createQuery("select m from Member m", Member.class)
                    .setHint("javax.persistence.fetchgraph", em.getEntityGraph("Member.withTeam"))
                    .getSingleResult();

            /*
             select
                member0_.id as id1_0_0_,
                team1_.id as id1_2_1_,
                member0_.name as name2_0_0_,
                member0_.team_id as team_id4_0_0_,
                member0_.vip as vip3_0_0_
            from
                Member member0_
            left outer join
                Team team1_
                    on member0_.team_id=team1_.id
             */
            assertNotNull(member);
        });
    }

    @Test
    void testDynamicEntityGraph() {
        doTransaction(em -> {
            Member member = new Member("oopty");
            em.persist(member);
        });

        doTransaction(em -> {
            EntityGraph<Member> graph = em.createEntityGraph(Member.class);
            graph.addAttributeNodes("team");

            Map<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.fetchgraph", graph);

            Member member = em.find(Member.class, 1L, hints);
            assertNotNull(member);

            /*
             select
                member0_.id as id1_0_0_,
                member0_.name as name2_0_0_,
                member0_.team_id as team_id4_0_0_,
                member0_.vip as vip3_0_0_,
                team1_.id as id1_2_1_
            from
                Member member0_
            left outer join
                Team team1_
                    on member0_.team_id=team1_.id
            where
                member0_.id=?
             */
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
