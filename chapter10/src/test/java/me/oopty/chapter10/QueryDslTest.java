package me.oopty.chapter10;

import com.querydsl.core.QueryModifiers;
import com.querydsl.jpa.impl.JPAQuery;
import me.oopty.chapter10.code.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;
import java.util.function.Consumer;

import static com.querydsl.jpa.JPAExpressions.avg;
import static me.oopty.chapter10.QMember.*;
import static me.oopty.chapter10.QProduct.*;
import static me.oopty.chapter10.QTeam.*;
import static org.assertj.core.api.Assertions.*;

public class QueryDslTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");

    }

    @Test
    void testSimpleQuery() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });

        doTransaction(em -> {
            JPAQuery<Member> jpaQuery = new JPAQuery<>(em);

            List<Member> members = jpaQuery.from(member)
                    .where(member.username.eq("oopty"))
                    .orderBy(member.username.desc())
                    .fetch();
            assertThat(members.size()).isEqualTo(2);
        });
    }

    @Test
    void testConditionalQuery() {
        doTransaction(em -> {
            Product product1 = new Product("product1", 1000, 2);
            Product product2 = new Product("product2", 2000, 3);
            Product product3 = new Product("product3", 3000, 2);

            em.persist(product1);
            em.persist(product2);
            em.persist(product3);
        });
        doTransaction(em -> {
            JPAQuery<Product> jpaQuery = new JPAQuery<>(em);

            List<Product> products = jpaQuery.from(product)
                    .where(product.stockAmount.goe(3).and(product.price.goe(2000)))
                    .fetch();

            assertThat(products.get(0).getName()).isEqualTo("product2");
        });
    }

    @Test
    void testPagination() {
        doTransaction(em -> {
            Product product1 = new Product("product1", 1000, 2);
            Product product2 = new Product("product2", 2000, 3);
            Product product3 = new Product("product3", 3000, 2);
            Product product4 = new Product("product4", 4000, 2);
            Product product5 = new Product("product5", 5000, 2);

            em.persist(product1);
            em.persist(product2);
            em.persist(product3);
            em.persist(product4);
            em.persist(product5);
        });
        doTransaction(em -> {
            JPAQuery<Product> jpaQuery = new JPAQuery<>(em);

            List<Product> products = jpaQuery.from(product)
                    .offset(1).limit(3)
                    .fetch();

            assertThat(products.size()).isEqualTo(3);
            assertThat(products.get(0).getName()).isEqualTo("product2");


            QueryModifiers queryModifiers = new QueryModifiers(3L, 1L);
            List<Product> products2 = jpaQuery.from(product)
                    .restrict(queryModifiers)
                    .fetch();

            assertThat(products2.size()).isEqualTo(3);
            assertThat(products2.get(0).getName()).isEqualTo("product2");
        });
    }

    @Test
    void testGroupBy() {
        doTransaction(em -> {
            Product product1 = new Product("product1", 1000, 2);
            Product product2 = new Product("product2", 2000, 3);
            Product product3 = new Product("product3", 2000, 2);
            Product product4 = new Product("product4", 4000, 2);
            Product product5 = new Product("product5", 4000, 2);

            em.persist(product1);
            em.persist(product2);
            em.persist(product3);
            em.persist(product4);
            em.persist(product5);
        });
        doTransaction(em -> {
            JPAQuery<Product> jpaQuery = new JPAQuery<>(em);
            List<Double> stockAmountAvg = jpaQuery.select(product.stockAmount.avg()).from(product)
                    .groupBy(product.price)
                    .having(product.price.goe(4000))
                    .fetch();

            Assertions.assertThat(stockAmountAvg.size()).isEqualTo(1);
            Assertions.assertThat(stockAmountAvg.get(0)).isEqualTo(2);
        });
    }

    @Test
    void testJoin() {
        doTransaction(em -> {
            Member member1 = new Member("oopty", 10);
            Member member2 = new Member("oopty", 10);
            Team team = new Team();
            team.setName("team1");
            member1.setTeam(team);
            member2.setTeam(team);
            team.getMember().add(member1);
            team.getMember().add(member2);
            em.persist(team);
            em.persist(member1);
            em.persist(member2);
        });
        doTransaction(em -> {
            JPAQuery<Member> jpaQuery = new JPAQuery<>(em);

            List<Member> members = jpaQuery.from(member)
                    .leftJoin(member.team, team).fetchJoin()
                    .fetch();

            Assertions.assertThat(members.size()).isEqualTo(2);
            /*
             select
                member0_.id as id1_0_0_,
                team1_.id as id1_3_1_,
                member0_.age as age2_0_0_,
                member0_.TEAM_ID as team_id4_0_0_,
                member0_.username as username3_0_0_,
                team1_.name as name2_3_1_
            from
                Member member0_
            left outer join
                Team team1_
                    on member0_.TEAM_ID=team1_.id
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
