package me.oopty.chapter7.identifyingrelation.case2.idclass;

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
             insert into PARENT3 (name, PARENT_ID) values (?, ?)
             insert into CHILD3 (name, CHILD_ID, PARENT_ID) values (?, ?, ?)
             insert into GrandChild (name, PARENT_ID, CHILD_ID, GRAND_CHILD_ID) values (?, ?, ?, ?)
             */
        });

        doTransaction(em -> {
            ChildId childId = new ChildId();
            childId.setId("c1");
            childId.setParent("p1");

            GrandChildId grandChildId = new GrandChildId();
            grandChildId.setId("gc1");
            grandChildId.setChild(childId);

            GrandChild child = em.find(GrandChild.class, grandChildId);
            assertThat(child.getChild().getParent().getName()).isEqualTo("oopty1");

            /*
             select
                grandchild0_.PARENT_ID as parent_i0_7_0_,
                grandchild0_.CHILD_ID as child_id0_7_0_,
                grandchild0_.GRAND_CHILD_ID as grand_ch1_7_0_,
                grandchild0_.PARENT_ID as parent_i3_7_0_,
                grandchild0_.CHILD_ID as child_id4_7_0_,
                grandchild0_.name as name2_7_0_,
                child1_.CHILD_ID as child_id1_6_1_,
                child1_.PARENT_ID as parent_i2_6_1_,
                child1_.name as name3_6_1_,
                parent2_.PARENT_ID as parent_i1_16_2_,
                parent2_.name as name2_16_2_
            from
                GrandChild grandchild0_
            inner join
                CHILD3 child1_
                    on grandchild0_.PARENT_ID=child1_.CHILD_ID
                    and grandchild0_.CHILD_ID=child1_.PARENT_ID
            inner join
                PARENT3 parent2_
                    on child1_.PARENT_ID=parent2_.PARENT_ID
            where
                grandchild0_.PARENT_ID=?
                and grandchild0_.CHILD_ID=?
                and grandchild0_.GRAND_CHILD_ID=?
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
