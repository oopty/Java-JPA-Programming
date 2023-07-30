package me.oopty.chapter7.identifyingrelation.case3;

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
        doTransaction(em -> {
            Parent parent = new Parent();
            parent.setParentId("p1");
            parent.setName("oopty1");

            Child child = new Child();
            child.setId("c1");
            child.setName("oopty2");
            child.setParent(parent);

            GrandChild grandChild = new GrandChild();
            grandChild.setId("gc1");
            grandChild.setChild(child);
            grandChild.setName("oopty3");

            em.persist(parent);
            em.persist(child);
            em.persist(grandChild);
            /*
             insert into PARENT5 (name, PARENT_ID) values (?, ?)
             insert into CHILD5 (name, PARENT_ID, CHILD_ID) values (?, ?, ?)
             insert into GRAND_CHILD3 (CHILD_ID, name, GRAND_CHILD_ID) values (?, ?, ?)
             */
        });

        doTransaction(em -> {
            GrandChild child = em.find(GrandChild.class, "gc1");
            assertThat(child.getChild().getParent().getName()).isEqualTo("oopty1");

            /*
             select
                grandchild0_.GRAND_CHILD_ID as grand_ch1_9_0_,
                grandchild0_.CHILD_ID as child_id3_9_0_,
                grandchild0_.name as name2_9_0_,
                child1_.CHILD_ID as child_id1_8_1_,
                child1_.name as name2_8_1_,
                child1_.PARENT_ID as parent_i3_8_1_,
                parent2_.PARENT_ID as parent_i1_22_2_,
                parent2_.name as name2_22_2_
            from
                GRAND_CHILD3 grandchild0_
            left outer join
                CHILD5 child1_
                    on grandchild0_.CHILD_ID=child1_.CHILD_ID
            left outer join
                PARENT5 parent2_
                    on child1_.PARENT_ID=parent2_.PARENT_ID
            where
                grandchild0_.GRAND_CHILD_ID=?
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
