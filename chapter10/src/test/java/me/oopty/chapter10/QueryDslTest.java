package me.oopty.chapter10;

import antlr.StringUtils;
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
import java.util.List;
import java.util.function.Consumer;

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

    @Test
    void testSubQuery() {
        doTransaction(em -> {
            Member member1 = new Member("oopty1", 10);
            Member member2 = new Member("oopty2", 10);
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
            JPAQuery<Team> jpaQuery = new JPAQuery<>(em);

            Team team = jpaQuery.from(QTeam.team)
                    .where(QTeam.team.member.contains(new JPAQuery<Member>(em).from(member).where(member.username.eq("oopty1")).fetchOne()))
                    .fetchOne();

            Assertions.assertThat(team.getName()).isEqualTo("team1");
        });
    }

    @Test
    void testProjection() {
        doTransaction(em -> {
            Member member1 = new Member("oopty1", 10);
            Member member2 = new Member("oopty2", 10);
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
            JPAQuery<Member> query = new JPAQuery<>(em);

            List<Tuple> results = query.select(member.username, member.age).from(member).fetch();
            Assertions.assertThat(results.size()).isEqualTo(2);

            Assertions.assertThat(results.get(0).get(member.username)).isEqualTo("oopty1");
            Assertions.assertThat(results.get(0).get(member.age)).isEqualTo(10);

            Assertions.assertThat(results.get(1).get(member.username)).isEqualTo("oopty2");
            Assertions.assertThat(results.get(1).get(member.age)).isEqualTo(10);
        });
    }

    @Test
    void testProjectionBeanPopulation() {
        doTransaction(em -> {
            Member member1 = new Member("oopty1", 10);
            Member member2 = new Member("oopty2", 10);
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
            JPAQuery<Member> query = new JPAQuery<>(em);

            List<UserDTO> results = query.select(Projections.bean(UserDTO.class, member.username, member.age)).from(member).fetch();
            Assertions.assertThat(results.size()).isEqualTo(2);

            List<UserDTO> results2 = query.select(Projections.fields(UserDTO.class, member.username, member.age)).from(member).fetch();
            Assertions.assertThat(results.size()).isEqualTo(2);

            List<UserDTO> results3 = query.select(Projections.constructor(UserDTO.class, member.username, member.age)).from(member).fetch();
            Assertions.assertThat(results.size()).isEqualTo(2);

            Assertions.assertThat(results.get(0).getUsername()).isEqualTo("oopty1");
            Assertions.assertThat(results.get(0).getAge()).isEqualTo(10);

            Assertions.assertThat(results.get(1).getUsername()).isEqualTo("oopty2");
            Assertions.assertThat(results.get(1).getAge()).isEqualTo(10);

            Assertions.assertThat(results2.get(0).getUsername()).isEqualTo("oopty1");
            Assertions.assertThat(results2.get(0).getAge()).isEqualTo(10);

            Assertions.assertThat(results2.get(1).getUsername()).isEqualTo("oopty2");
            Assertions.assertThat(results2.get(1).getAge()).isEqualTo(10);

            Assertions.assertThat(results3.get(0).getUsername()).isEqualTo("oopty1");
            Assertions.assertThat(results3.get(0).getAge()).isEqualTo(10);

            Assertions.assertThat(results3.get(1).getUsername()).isEqualTo("oopty2");
            Assertions.assertThat(results3.get(1).getAge()).isEqualTo(10);
        });
    }

    @Test
    void testBatchQuery() {
        doTransaction(em -> {
            Member member1 = new Member("oopty1", 10);
            Member member2 = new Member("oopty2", 10);
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
            JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, member);
            jpaUpdateClause.where(member.username.eq("oopty1"))
                    .set(member.age, member.age.add(1))
                    .execute();

            JPADeleteClause jpaDeleteClause = new JPADeleteClause(em, member);
            jpaDeleteClause.where(member.username.eq("oopty2"))
                    .execute();
        });
        doTransaction(em -> {
            TypedQuery<Member> query = em.createQuery("select m from Member m where m.username = 'oopty1'", Member.class);
            TypedQuery<Member> query2 = em.createQuery("select m from Member m where m.username = 'oopty2'", Member.class);
            Member singleResult = query.getSingleResult();
            List<Member> resultList = query2.getResultList();

            Assertions.assertThat(singleResult.getAge()).isEqualTo(11);
            Assertions.assertThat(resultList.size()).isEqualTo(0);
        });
    }

    @Test
    void testDynamicQuery() {
        doTransaction(em -> {
            Product product1 = new Product("product1", 1000, 2);
            Product product2 = new Product("product2", 2000, 2);
            Product product3 = new Product("product3", 3000, 2);

            em.persist(product1);
            em.persist(product2);
            em.persist(product3);
        });
        doTransaction(em -> {
            SearchParam param = new SearchParam("", 2000);
            Product result = getProduct(em, param);
            Assertions.assertThat(result.getName()).isEqualTo("product2");
        });
    }

    @Test
    void testMethodDelegation() {
        doTransaction(em -> {
            Product product1 = new Product("product1", 1000, 2);
            Product product2 = new Product("product2", 2000, 2);
            Product product3 = new Product("product3", 3000, 2);

            em.persist(product1);
            em.persist(product2);
            em.persist(product3);
        });
        doTransaction(em -> {
            List<Member> members = new JPAQuery<Member>(em).from(product)
                    .where(product.isExpensive(1999))
                    .fetch();

            Assertions.assertThat(members.size()).isEqualTo(2);
        });
    }

    private static Product getProduct(EntityManager em, SearchParam param) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(!(param.getName().isBlank() || param.getName().isEmpty())) {
            booleanBuilder.and(product.name.eq(param.getName()));
        }

        if(param.getPrice() != null) {
            booleanBuilder.and(product.price.eq(param.getPrice()));
        }

        JPAQuery<Product> jpaQuery = new JPAQuery<>(em);
        Product result = jpaQuery.from(product)
                .where(booleanBuilder)
                .fetchOne();
        return result;
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
