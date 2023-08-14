import me.oopty.chapter14.domain.Member;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class ConverterTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

    @Test
    void testBooleanConverter() {
        /*
         * create table Member (
               id bigint not null,
                name varchar(255),
                vip varchar(255),
                team_id bigint,
                primary key (id)
            )
         */
        doTransaction(em -> {
            Member member = new Member("name");
            member.setVip(true);

            em.persist(member);
        });
        doTransaction(em -> {
            Member member = em.find(Member.class, 1L);
            assertThat(member.getVip()).isTrue();
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
