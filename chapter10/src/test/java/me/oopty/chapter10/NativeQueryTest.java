package me.oopty.chapter10;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import me.oopty.chapter10.code.SearchParam;
import me.oopty.chapter10.code.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;

import static me.oopty.chapter10.QMember.member;
import static me.oopty.chapter10.QProduct.product;
import static me.oopty.chapter10.QTeam.team;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class NativeQueryTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");

    }

    @Test
    void testBasicUsage() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("minjun", 12);

            Address homeAddress = new Address("city1", "street1", "zipcode1");
            Product product1 = new Product("product1", 1000, 3);
            Order order1 = new Order(10, member1, homeAddress, product1);
            Order order2 = new Order(10, member1, homeAddress, product1);
            Order order3 = new Order(10, member2, homeAddress, product1);


            em.persist(member1);
            em.persist(member2);
            em.persist(product1);
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
        });

        doTransaction(em -> {
            // case1
            List<Object[]> resultList = em.createNativeQuery("select m.id, m.username, m.age from member m where m.username = :username")
                    .setParameter("username", "oopty")
                    .getResultList();
            Object[] result = resultList.get(0);
            assertThat(result[1]).isEqualTo("oopty");

            // case2
            Query query = em.createNativeQuery("select m.id, m.username, m.age, m.TEAM_ID from member m where m.username = :username", Member.class);
            query.setParameter("username", "oopty");
            Member singleResult = (Member) query.getSingleResult();
            assertThat(singleResult.getUsername()).isEqualTo("oopty");

            // case3
            List<Object[]> results = em.createNativeQuery("select m.id, m.username, m.age, m.TEAM_ID, OM.ORDER_COUNT from member m left outer join (select o.MEMBER_ID as id, count(*) AS ORDER_COUNT from orders o group by o.MEMBER_ID) OM on m.id = OM.id", "memberWithOrderCount")
                    .getResultList();

            for(Object[] row : results) {
                Member member1 = (Member) row[0];
                BigInteger orderCount = (BigInteger) row[1];

                System.out.println("member: " + member1);
                System.out.println("orderCount: " + orderCount);
            }
        });
    }

    @Test
    void testNamedNativeQuery() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("minjun", 12);

            Address homeAddress = new Address("city1", "street1", "zipcode1");
            Product product1 = new Product("product1", 1000, 3);
            Order order1 = new Order(10, member1, homeAddress, product1);
            Order order2 = new Order(10, member1, homeAddress, product1);
            Order order3 = new Order(10, member2, homeAddress, product1);


            em.persist(member1);
            em.persist(member2);
            em.persist(product1);
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
        });

        doTransaction(em -> {
            List<Object[]> resultList = em.createNamedQuery("Member.memberWithOrderCount").getResultList();

            for(Object[] row : resultList) {
                Member member1 = (Member) row[0];
                BigInteger orderCount = (BigInteger) row[1];

                System.out.println("member: " + member1);
                System.out.println("orderCount: " + orderCount);
            }
        });
    }

    @Test
    void testBatchQuery() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("minjun", 12);

            Address homeAddress = new Address("city1", "street1", "zipcode1");
            Product product1 = new Product("product1", 1000, 3);
            Order order1 = new Order(10, member1, homeAddress, product1);
            Order order2 = new Order(20, member1, homeAddress, product1);
            Order order3 = new Order(30, member2, homeAddress, product1);


            em.persist(member1);
            em.persist(member2);
            em.persist(product1);
            em.persist(order1);
            em.persist(order2);
            em.persist(order3);
        });

        doTransaction(em -> {
            int resultCount = em.createQuery("update Member m set m.username = 'ytpoo' where m.username = :username")
                    .setParameter("username", "oopty")
                    .executeUpdate();

            Assertions.assertThat(resultCount).isEqualTo(1);
        });

        doTransaction(em -> {
            int resultCount = em.createQuery("delete from Order o where o.orderAmount = :orderAmount")
                    .setParameter("orderAmount", 20)
                    .executeUpdate();

            Assertions.assertThat(resultCount).isEqualTo(1);
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
