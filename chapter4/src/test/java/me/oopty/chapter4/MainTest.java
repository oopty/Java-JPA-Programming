package me.oopty.chapter4;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Consumer;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void testColumn() {
        doTransaction(em -> {
            Member member = new Member();
            member.setId("id1");
            member.setUsername("username");
            member.setAge(30);
            member.setRoleType(RoleType.USER);
            member.setCreatedDate(Date.from(Instant.now()));
            member.setModifiedAt(LocalDateTime.now());
            member.setDescription("description1");
            member.setTemp("transient property");

            em.persist(member);
        });

        doTransaction(em -> {
            Member singleResult = em.createQuery("select m from Member m where id = :id", Member.class)
                    .setParameter("id", "id1")
                    .getSingleResult();

            System.out.println(singleResult);
        });
    }

    @Test
    void testUniqueConstraint() {
        Assertions.assertThatThrownBy(() ->
            doTransaction(em -> {
                Member member1 = new Member();
                member1.setId("id1");
                member1.setUsername("username");
                member1.setAge(30);
                member1.setRoleType(RoleType.USER);
                member1.setCreatedDate(Date.from(Instant.now()));
                member1.setModifiedAt(LocalDateTime.now());
                member1.setDescription("description1");
                member1.setTemp("transient property");


                Member member2 = new Member();
                member2.setId("id2");
                member2.setUsername("username");
                member2.setAge(30);
                member2.setRoleType(RoleType.USER);
                member2.setCreatedDate(Date.from(Instant.now()));
                member2.setModifiedAt(LocalDateTime.now());
                member2.setDescription("description1");
                member2.setTemp("transient property");

                em.persist(member1);
                em.persist(member2);
            })
        ).isInstanceOf(RollbackException.class);
    }

    @Test
    void testGeneratedValueIdentity() {
        doTransaction(em -> {
            MemberV2 member = new MemberV2();
            member.setUsername("username");
            member.setAge(30);
            member.setRoleType(RoleType.USER);
            member.setCreatedDate(Date.from(Instant.now()));
            member.setModifiedAt(LocalDateTime.now());
            member.setDescription("description1");
            member.setTemp("transient property");

            Assertions.assertThat(member.getId()).isNull();

            System.out.println("Before persist");
            em.persist(member); // 데이터베이스에서 Id를 가져오기 위해 쓰기 지연 전략 사용안함
            System.out.println("After persist");
            Assertions.assertThat(member.getId()).isNotNull();
        });
    }

    @Test
    void testGeneratedValueSequence() {
        doTransaction(em -> {
            MemberV3 member = new MemberV3();
            member.setUsername("username");
            member.setAge(30);
            member.setRoleType(RoleType.USER);
            member.setCreatedDate(Date.from(Instant.now()));
            member.setModifiedAt(LocalDateTime.now());
            member.setDescription("description1");
            member.setTemp("transient property");

            Assertions.assertThat(member.getId()).isNull();

            System.out.println("Before persist");
            em.persist(member); // call next value for MEMBER_SEQ_GENERATOR 시퀀스 불러옴, 쓰기지연은 됨
            System.out.println("After persist");
            Assertions.assertThat(member.getId()).isNotNull();
        });
    }

    @Test
    void testGeneratedValueSequence_allocationSize() {
        doTransaction(em -> {
            for(int i = 0; i < 20; i++) {
                MemberV3 member = new MemberV3();
                member.setUsername("username");
                member.setAge(30);
                member.setRoleType(RoleType.USER);
                member.setCreatedDate(Date.from(Instant.now()));
                member.setModifiedAt(LocalDateTime.now());
                member.setDescription("description1");
                member.setTemp("transient property");

                System.out.println("Before persist");
                em.persist(member); // call next value for MEMBER_SEQ_GENERATOR 시퀀스 불러옴, 쓰기지연은 됨
                Assertions.assertThat(member.getId()).isEqualTo(i + 1);
                System.out.println("After persist");
            }
            // call next sequence가 총 3번 불림(1, 11, 21), 20까지 키를 선점하니, 21까지 가져와야함
        });
    }

    @Test
    void testGeneratedValueTable() {
        doTransaction(em -> {
            MemberV4 member = new MemberV4();
            member.setUsername("username");
            member.setAge(30);
            member.setRoleType(RoleType.USER);
            member.setCreatedDate(Date.from(Instant.now()));
            member.setModifiedAt(LocalDateTime.now());
            member.setDescription("description1");
            member.setTemp("transient property");

            System.out.println("Before persist");
            /*
            총 2번 불림
            1. select tbl.next_val from MY_SEQUENCES tbl where tbl.sequence_name=? for update
            2. update MY_SEQUENCES set next_val=? where next_val=? and sequence_name=?
             */
            em.persist(member);
            System.out.println("After persist");
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
