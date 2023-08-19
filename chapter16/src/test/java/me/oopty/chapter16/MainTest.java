package me.oopty.chapter16;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

    EntityManagerFactory emf;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("jpabook");
    }

    @Test
    void testNoneModeType() throws InterruptedException {
        MyThreadUncaughtException handler = new MyThreadUncaughtException();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        doTransaction(em -> {
            Board board = new Board("title1");
            em.persist(board);
        });

        Thread transaction1 = new Thread(() -> doTransaction(em -> {
            Board board = em.find(Board.class, 1L, LockModeType.NONE);
            sleep(1000);
            board.setTitle("titleTransaction1");
        }));

        Thread transaction2 = new Thread(() -> doTransaction(em -> {
            Board board = em.find(Board.class, 1L);
            board.setTitle("titleTransaction2");
        }));

        transaction1.start();
        transaction2.start();
        transaction1.join();
        transaction2.join();

        Throwable exceptionFrom = handler.getExceptionFrom(transaction1);
        assertThat(exceptionFrom).isInstanceOf(RollbackException.class);
    }

    @Test
    void testOptimisticLoForceIncrement() throws InterruptedException {
        MyThreadUncaughtException handler = new MyThreadUncaughtException();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        doTransaction(em -> {
            Board board = new Board("title1");
            em.persist(board);
        });

        Thread transaction1 = new Thread(() -> doTransaction(em -> {
            em.find(Board.class, 1L, LockModeType.OPTIMISTIC);
            sleep(1000);
        }));

        Thread transaction2 = new Thread(() -> doTransaction(em -> {
            Board board = em.find(Board.class, 1L);
            board.setTitle("titleTransaction2");
        }));

        transaction1.start();
        transaction2.start();
        transaction1.join();
        transaction2.join();

        Throwable exceptionFrom = handler.getExceptionFrom(transaction1);
        assertThat(exceptionFrom).isInstanceOf(RollbackException.class);
    }

    @Test
    void testOPTIMISTIC_FORCE_INCREMENT() {
        doTransaction(em -> {
            Board board = new Board("title1");

            Attachement attachement = new Attachement("iris.csv", "/Users/user/Donwloads");

            attachement.setBoard(board);
            board.getAttchments().add(attachement);
            em.persist(attachement);
            em.persist(board);
        });

        doTransaction(em -> {
            Board board = em.find(Board.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            board.getAttchments().get(0).setFilename("iris2.csv");
            /*
             1. update Attachement set board_id=?, filename=?, path=? where id=?
             2. update Board set version=? where id=? and version=?
             */
            assertThat(board.getVersion()).isEqualTo(0);
        });

        doTransaction(em -> {
            Board board = em.find(Board.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            assertThat(board.getVersion()).isEqualTo(1);
        });
    }

    @Test
    void testPessimisticWriteLock() throws InterruptedException {
        MyThreadUncaughtException handler = new MyThreadUncaughtException();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        doTransaction(em -> {
            Board board = new Board("title1");
            em.persist(board);
        });

        Thread transaction1 = new Thread(() -> doTransaction(em -> {
            em.find(Board.class, 1L, LockModeType.PESSIMISTIC_WRITE);
            sleep(5000);
            System.out.println("lock release");
        }));

        Thread transaction2 = new Thread(() -> doTransaction(em -> {
            sleep(100);
            Board board = em.find(Board.class, 1L, LockModeType.NONE);
            board.setTitle("titleTransaction2");
        }));

        transaction1.start();
        transaction2.start();
        transaction1.join();
        transaction2.join();

        doTransaction(em -> {
            Board board = em.find(Board.class, 1L);
            assertThat(board.getTitle()).isEqualTo("title1");
            assertThat(board.getVersion()).isEqualTo(0);
        });

        Throwable exceptionFrom = handler.getExceptionFrom(transaction2);
        assertThat(exceptionFrom).isInstanceOf(PessimisticLockException.class);
    }

    @Test
    void testPESSIMISTIC_FORCE_INCREMENT() throws InterruptedException {
        MyThreadUncaughtException handler = new MyThreadUncaughtException();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        doTransaction(em -> {
            Board board = new Board("title1");
            em.persist(board);
        });

        Thread transaction1 = new Thread(() -> doTransaction(em -> {
            em.find(Board.class, 1L, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            sleep(5000);
            System.out.println("lock release");
        }));

        Thread transaction2 = new Thread(() -> doTransaction(em -> {
            sleep(100);
            Board board = em.find(Board.class, 1L, LockModeType.NONE);
            board.setTitle("titleTransaction2");
        }));

        transaction1.start();
        transaction2.start();
        transaction1.join();
        transaction2.join();

        doTransaction(em -> {
            Board board = em.find(Board.class, 1L);
            assertThat(board.getTitle()).isEqualTo("title1");
            assertThat(board.getVersion()).isEqualTo(1);
        });

        Throwable exceptionFrom = handler.getExceptionFrom(transaction2);
        assertThat(exceptionFrom).isInstanceOf(PessimisticLockException.class);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    private static class MyThreadUncaughtException implements Thread.UncaughtExceptionHandler {
        private Map<String, Throwable> threadExceptionMap = new ConcurrentHashMap<>();

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            threadExceptionMap.put(t.getName(), e.getCause());
        }

        public Throwable getExceptionFrom(Thread thread) {
            return threadExceptionMap.get(thread.getName());
        }
    }
}
