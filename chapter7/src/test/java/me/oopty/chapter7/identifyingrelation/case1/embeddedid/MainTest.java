package me.oopty.chapter7.identifyingrelation.case1.embeddedid;

import org.assertj.core.api.Assertions;
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
            ParentId parentId = new ParentId();
            parentId.setParentId1("p1");
            parentId.setParentId2("p2");

            Parent parent = new Parent();
            parent.setParentId(parentId);
            parent.setName("oopty");

            Child child = new Child();
            child.setId("c1");
            child.setParent(parent);

            em.persist(parent);
            em.persist(child);
            /*
             insert into PARENT2 (name, PARENT_ID1, PARENT_ID2) values (?, ?, ?)
             insert into CHILD2 (PARENT_ID1, PARENT_ID2, CHILD_ID) values (?, ?, ?)
             */
        });

        doTransaction(em -> {
            ParentId parentId = new ParentId();
            parentId.setParentId1("p1");
            parentId.setParentId2("p2");

            Parent parent = em.find(Parent.class, parentId);
            Assertions.assertThat(parent.getName()).isEqualTo("oopty");

            /*
             select
                parent0_.PARENT_ID1 as parent_i1_13_0_,
                parent0_.PARENT_ID2 as parent_i2_13_0_,
                parent0_.name as name3_13_0_
            from
                PARENT2 parent0_
            where
                parent0_.PARENT_ID1=?
                and parent0_.PARENT_ID2=?
             */
        });

        doTransaction(em -> {
            Child child = em.find(Child.class, "c1");
            assertThat(child.getParent().getName()).isEqualTo("oopty");

            /*
             select
                child0_.CHILD_ID as child_id1_5_0_,
                child0_.PARENT_ID1 as parent_i2_5_0_,
                child0_.PARENT_ID2 as parent_i3_5_0_,
                parent1_.PARENT_ID1 as parent_i1_13_1_,
                parent1_.PARENT_ID2 as parent_i2_13_1_,
                parent1_.name as name3_13_1_
            from
                CHILD2 child0_
            left outer join
                PARENT2 parent1_
                    on child0_.PARENT_ID1=parent1_.PARENT_ID1
                    and child0_.PARENT_ID2=parent1_.PARENT_ID2
            where
                child0_.CHILD_ID=?
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
