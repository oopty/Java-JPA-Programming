package me.oopty.chapter7.secondarytable;

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

        /*
         1. create table BOARD_DETAIL2 (
               content varchar(255),
                BOARD_DETAIL_ID bigint not null,
                primary key (BOARD_DETAIL_ID)
            )

         2. create table BOARD2 (
               id bigint not null,
                title varchar(255),
                primary key (id)
            )
         */
        doTransaction(em -> {
            Board board = new Board();
            board.setTitle("title");
            board.setContent("content");

            em.persist(board);
        });

        doTransaction(em -> {
            Board board = em.find(Board.class, 1L);

            assertThat(board.getTitle()).isEqualTo("title");
            assertThat(board.getContent()).isEqualTo("content");
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
