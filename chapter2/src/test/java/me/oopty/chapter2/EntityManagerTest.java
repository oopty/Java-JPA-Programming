package me.oopty.chapter2;

import me.oopty.chapter2.Member;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class EntityManagerTest {

    @Test
    public void entityManager_crud() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpabook");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction tx = entityManager.getTransaction();

        try {
            tx.begin();
            logic(entityManager);
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
        entityManagerFactory.close();
    }

    private void logic(EntityManager entityManager) {
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("oopty");
        member.setAge(10);

        entityManager.persist(member);

        member.setAge(30);

        Member findMember = entityManager.find(Member.class, id);
        System.out.println("findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        List<Member> findMembers = entityManager.createQuery("select m from Member m", Member.class).getResultList();
        System.out.println("members.size=" + findMembers.size());

        entityManager.remove(member);
    }
}
