package me.oopty.chapter7.identifyingrelation.case4;

import me.oopty.chapter7.identifyingrelation.case3.Child;
import me.oopty.chapter7.identifyingrelation.case3.GrandChild;
import me.oopty.chapter7.identifyingrelation.case3.Parent;
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
            Board board = new Board();
            board.setTitle("title");

            BoardDetail boardDetail = new BoardDetail();
            boardDetail.setContent("content");
            boardDetail.setBoard(board);

            em.persist(board);
            em.persist(boardDetail);
            /*
             insert into Board (title, id) values (?, ?)
             insert into BoardDetail (content, BOARD_ID) values (?, ?)
             */
        });

        doTransaction(em -> {
//            Board board = em.find(Board.class, 1L);
            BoardDetail boardDetail = em.find(BoardDetail.class, 1L);
            assertThat(boardDetail.getBoard().getTitle()).isEqualTo("title");


            /*
             1. select
                    boarddetai0_.BOARD_ID as board_id1_3_0_,
                    boarddetai0_.content as content2_3_0_
                from
                    BoardDetail boarddetai0_
                where
                    boarddetai0_.BOARD_ID=?


             2. select
                    board0_.id as id1_2_0_,
                    board0_.title as title2_2_0_,
                    boarddetai1_.BOARD_ID as board_id1_3_1_,
                    boarddetai1_.content as content2_3_1_
                from
                    Board board0_
                left outer join
                    BoardDetail boarddetai1_
                        on board0_.id=boarddetai1_.BOARD_ID
                where
                    board0_.id=?
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
