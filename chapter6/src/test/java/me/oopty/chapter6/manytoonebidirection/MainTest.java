package me.oopty.chapter6.manytoonebidirection;

import me.oopty.chapter6.manytomany.MemberV6;
import me.oopty.chapter6.manytomany.Product;
import me.oopty.chapter6.manytomany.associateentity.Member;
import me.oopty.chapter6.manytomany.associateentity.MemberProduct;
import me.oopty.chapter6.manytomany.associateentity.MemberProductId;
import me.oopty.chapter6.manytomany.associateentity.ProductV2;
import me.oopty.chapter6.manytoonebidrection.MemberV2;
import me.oopty.chapter6.manytoonebidrection.TeamV2;
import me.oopty.chapter6.onetomany.MemberV3;
import me.oopty.chapter6.onetomany.TeamV3;
import me.oopty.chapter6.onetomanybidirection.MemberV4;
import me.oopty.chapter6.onetomanybidirection.TeamV4;
import me.oopty.chapter6.onetoone.Locker;
import me.oopty.chapter6.onetoone.MemberV5;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Consumer;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void testBidirectionalManyToOne() {
        doTransaction(em -> {
            TeamV2 team1 = new TeamV2();
            team1.setId(1L);
            team1.setName("team1");
            TeamV2 team2 = new TeamV2();
            team2.setId(2L);
            team2.setName("team2");

            MemberV2 member1 = new MemberV2();
            member1.setId(1L);
            member1.setUsername("oopty");
            member1.setTeam(team1);

            MemberV2 member2 = new MemberV2();
            member2.setId(2L);
            member2.setUsername("oopty");
            team2.addMember(member2);

            em.persist(team1);
            em.persist(team2);

            em.persist(member1);
            em.persist(member2);
        });
        /*
         1. insert into TEAM_V2 (name, TEAM_ID) values (?, ?)
         2. insert into TEAM_V2 (name, TEAM_ID) values (?, ?)
         3. insert into MEMBER_V2 (TEAM_ID, username, MEMBER_ID) values (?, ?, ?)
         4. insert into MEMBER_V2 (TEAM_ID, username, MEMBER_ID) values (?, ?, ?)
         */

        doTransaction(em -> {
            MemberV2 member1 = em.find(MemberV2.class, 1L);
            MemberV2 member2 = em.find(MemberV2.class, 2L);

            Assertions.assertThat(member1.getTeam().getName()).isEqualTo("team1");
            Assertions.assertThat(member2.getTeam().getName()).isEqualTo("team2");
        });
    }

    @Test
    void testOneToMany() {
        doTransaction(em -> {
            TeamV3 team = new TeamV3();
            team.setId(1L);
            team.setName("team");

            MemberV3 member1 = new MemberV3();
            member1.setId(1L);
            member1.setUsername("oopty1");

            MemberV3 member2 = new MemberV3();
            member2.setId(2L);
            member2.setUsername("oopty2");

            team.addMember(member1);
            team.addMember(member2);

            em.persist(team);

            em.persist(member1);
            em.persist(member2);
        });

        /*
         1. insert into TEAM_V3 (name, TEAM_ID) values (?, ?)
         2. insert into MEMBER_V3 (username, MEMBER_ID) values (?, ?)
         3. insert into MEMBER_V3 (username, MEMBER_ID) values (?, ?)
         4. update MEMBER_V3 set TEAM_ID=? where MEMBER_ID=?
         5. update MEMBER_V3 set TEAM_ID=? where MEMBER_ID=?
         */

        doTransaction(em -> {
            TeamV3 team = em.find(TeamV3.class, 1L);

            Assertions.assertThat(team.getMembers().get(0).getUsername()).isEqualTo("oopty1");
            Assertions.assertThat(team.getMembers().get(1).getUsername()).isEqualTo("oopty2");
        });

        /* OneToMany는 기본이 lazy fetch
         1. select
                teamv3x0_.TEAM_ID as team_id1_6_0_,
                teamv3x0_.name as name2_6_0_
            from
                TEAM_V3 teamv3x0_
            where
                teamv3x0_.TEAM_ID=?

         2. select
                members0_.TEAM_ID as team_id3_2_0_,
                members0_.MEMBER_ID as member_i1_2_0_,
                members0_.MEMBER_ID as member_i1_2_1_,
                members0_.username as username2_2_1_
            from
                MEMBER_V3 members0_
            where
                members0_.TEAM_ID=?
         */
    }

    @Test
    void testBidirectionalOneToMany() {
        doTransaction(em -> {
            TeamV4 team = new TeamV4();
            team.setId(1L);
            team.setName("team");

            MemberV4 member1 = new MemberV4();
            member1.setId(1L);
            member1.setUsername("oopty1");

            MemberV4 member2 = new MemberV4();
            member2.setId(2L);
            member2.setUsername("oopty2");

            team.addMember(member1);
            team.addMember(member2);

            em.persist(team);

            em.persist(member1);
            em.persist(member2);
        });

        /*
         1. insert into TEAM_V4 (name, TEAM_ID) values (?, ?)
         2. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         3. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         4. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         5. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         */

        doTransaction(em -> {
            TeamV4 team = em.find(TeamV4.class, 1L);

            Assertions.assertThat(team.getMembers().get(0).getUsername()).isEqualTo("oopty1");
            Assertions.assertThat(team.getMembers().get(1).getUsername()).isEqualTo("oopty2");
        });
    }

    @Test
    void testBidirectionalOneToOne() {
        doTransaction(em -> {
            MemberV5 member = new MemberV5();
            member.setId(1L);
            member.setUsername("oopty");

            Locker locker = new Locker();
            locker.setId(1L);
            locker.setMember(member);

            em.persist(member);
            em.persist(locker);

            MemberV5 member2 = new MemberV5();
            member2.setId(2L);
            member2.setUsername("oopty");

            Locker locker2 = new Locker();
            locker2.setId(2L);
            member2.setLocker(locker2);

            em.persist(member2);
            em.persist(locker2);
        });

        /*
         1. insert into TEAM_V4 (name, TEAM_ID) values (?, ?)
         2. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         3. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         4. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         5. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         */

        doTransaction(em -> {
            MemberV5 member = em.find(MemberV5.class, 1L);
            Assertions.assertThat(member.getLocker().get().getId()).isEqualTo(1L);

            MemberV5 member2 = em.find(MemberV5.class, 2L);
            Assertions.assertThat(member2.getLocker().get().getId()).isEqualTo(2L);
        });
    }

    @Test
    void testBidirectionalManyToMany() {
        doTransaction(em -> {
            MemberV6 member = new MemberV6();
            member.setId(1L);
            member.setUsername("oopty");

            Product product = new Product();
            product.setId(1L);
            product.setName("product1");

            product.addMember(member);

            em.persist(member);
            em.persist(product);

            MemberV6 member2 = new MemberV6();
            member2.setId(2L);
            member2.setUsername("oopty");

            Product product2 = new Product();
            product2.setId(2L);
            product2.setName("product2");

            product2.addMember(member2);

            em.persist(member2);
            em.persist(product2);
        });

        /*
         1. insert into TEAM_V4 (name, TEAM_ID) values (?, ?)
         2. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         3. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         4. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         5. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         */

        doTransaction(em -> {
            MemberV6 member = em.find(MemberV6.class, 1L);
            Assertions.assertThat(member.getProducts().get(0).getId()).isEqualTo(1L);

            MemberV6 member2 = em.find(MemberV6.class, 2L);
            Assertions.assertThat(member2.getProducts().get(0).getId()).isEqualTo(2L);
        });
    }

    @Test
    void testAssociateEntity() {
        doTransaction(em -> {
            Member member = new Member();
            member.setId(1L);
            member.setUsername("oopty");

            ProductV2 product = new ProductV2();
            product.setId(1L);
            product.setName("product");

            MemberProduct memberProduct = new MemberProduct();
            memberProduct.setMember(member);
            memberProduct.setProduct(product);
            memberProduct.setOrderAmount(1);

            em.persist(member);
            em.persist(product);
            em.persist(memberProduct);
        });

        /*
         1. insert into TEAM_V4 (name, TEAM_ID) values (?, ?)
         2. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         3. insert into MEMBER_V4 (username, MEMBER_ID) values (?, ?)
         4. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         5. update MEMBER_V4 set TEAM_ID=? where MEMBER_ID=?
         */

        doTransaction(em -> {
            MemberProductId memberProductId = new MemberProductId();
            memberProductId.setProduct(1L);
            memberProductId.setMember(1L);

            MemberProduct memberProduct = em.find(MemberProduct.class, memberProductId);

            Assertions.assertThat(memberProduct.getProduct().getId()).isEqualTo(1L);
            Assertions.assertThat(memberProduct.getMember().getId()).isEqualTo(1L);
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
