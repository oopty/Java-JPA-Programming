package me.oopty.chapter7.mappedsuperclass.idclass;

import me.oopty.chapter7.identifyingrelation.idclass.Child;
import me.oopty.chapter7.identifyingrelation.idclass.Parent;
import me.oopty.chapter7.identifyingrelation.idclass.ParentId;
import me.oopty.chapter7.inheritance.v3.Album;
import me.oopty.chapter7.inheritance.v3.Book;
import me.oopty.chapter7.inheritance.v3.Movie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.nio.channels.SelectableChannel;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

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
            parent.setParentId1("p1");
            parent.setParentId2("p2");
            parent.setName("oopty");

            Child child = new Child();
            child.setId("c1");
            child.setParent(parent);

            em.persist(parent);
            em.persist(child);
            /*
             insert into Parent (name, PARENT_ID1, PARENT_ID2) values (?, ?, ?)
             insert into Child (PARENT_ID1, PARENT_ID2, CHILD_ID) values (?, ?, ?)
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
                parent0_.PARENT_ID1 as parent_i1_10_0_,
                parent0_.PARENT_ID2 as parent_i2_10_0_,
                parent0_.name as name3_10_0_
            from
                Parent parent0_
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
                child0_.CHILD_ID as child_id1_4_0_,
                child0_.PARENT_ID1 as parent_i2_4_0_,
                child0_.PARENT_ID2 as parent_i3_4_0_,
                parent1_.PARENT_ID1 as parent_i1_11_1_,
                parent1_.PARENT_ID2 as parent_i2_11_1_,
                parent1_.name as name3_11_1_
            from
                Child child0_
            left outer join
                Parent parent1_
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
