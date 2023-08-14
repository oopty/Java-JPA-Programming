import me.oopty.chapter14.domain.Member;
import me.oopty.chapter14.domain.Team;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public class ListenerTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

    @Test
    void testEntityListener() {
        doTransaction(em -> {
            Member member = new Member("oopty");
            em.persist(member);
        });
        doTransaction(em -> {
            Member member = em.find(Member.class, 1L);
            em.remove(member);
        });
    }
    @Test
    void testEntityListenerClass() {
        doTransaction(em -> {
            Team team = new Team();
            em.persist(team);
        });
        doTransaction(em -> {
            Team team = em.find(Team.class, 1L);
            em.remove(team);
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
