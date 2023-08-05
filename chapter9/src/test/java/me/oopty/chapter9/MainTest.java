package me.oopty.chapter9;

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
    void testMember() {

        doTransaction(em -> {
            Member member = new Member(
                    1L,
                    "oopty",
                    new Address("street1", "city1", "zipcode1"),
                    new Address("street2", "city2", "zipcode2")
            );

            em.persist(member);
        });

        doTransaction(em -> {
            Member member = em.find(Member.class, 1L);

            assertThat(member.getHomeAddress().getCity()).isEqualTo("city1");
        });
    }

    @Test
    void testElementCollection() {

        doTransaction(em -> {
            Member member = new Member(1L, "oopty",
                    new Address("street1", "city1", "zipcode1"),
                    new Address("street2", "city2", "zipcode2"));

            member.getFavoriteFoods().add("짬뽕");
            member.getFavoriteFoods().add("짜장");
            member.getFavoriteFoods().add("탕수육");

            em.persist(member);
            /*
             1. insert
                into
                    Member
                    (COMPANY_CITY, COMPANY_STREET, COMPANY_ZIPCODE, city, street, zipcode, name, id)
                values
                    (?, ?, ?, ?, ?, ?, ?, ?)

             2. insert
                into
                    FAVORITE_FOODS
                    (MEMBER_ID, FOOD_NAME)
                values
                    (?, ?)

             3. insert
                into
                    FAVORITE_FOODS
                    (MEMBER_ID, FOOD_NAME)
                values
                    (?, ?)

             4. insert
                into
                    FAVORITE_FOODS
                    (MEMBER_ID, FOOD_NAME)
                values
                    (?, ?)
             */
        });
        doTransaction(em -> {
            Member member = em.find(Member.class, 1L);
            member.getFavoriteFoods().remove("탕수육");
            member.getFavoriteFoods().add("치킨");

            /*
             1. delete from
                    FAVORITE_FOODS
                where
                    MEMBER_ID=?

             2. insert into
                    FAVORITE_FOODS
                    (MEMBER_ID, FOOD_NAME)
                values
                    (?, ?)

             3. insert into
                    FAVORITE_FOODS
                    (MEMBER_ID, FOOD_NAME)
                values
                    (?, ?)

             4. insert into
                    FAVORITE_FOODS
                    (MEMBER_ID, FOOD_NAME)
                values
                    (?, ?)
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
