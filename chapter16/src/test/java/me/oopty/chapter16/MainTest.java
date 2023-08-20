package me.oopty.chapter16;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
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

    @Test
    void testCache() {
        doTransaction(em -> {
            Board board = new Board("title1");
            em.persist(board);
        });

        // entity manager에 전역으로 적용
        doTransaction(em -> {
            em.setProperty("javax.persistence.retrieveMode", CacheRetrieveMode.USE);
            em.setProperty("javax.persistence.storeMode", CacheStoreMode.USE);

            em.find(Board.class, 1L);
        });

        // find 할 때 적용
        doTransaction(em -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.retrieveMode", CacheRetrieveMode.USE);
            properties.put("javax.persistence.storeMode", CacheStoreMode.USE);

            em.find(Board.class, 1L, properties);
        });

        // refresh할 때 적용
        doTransaction(em -> {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("javax.persistence.retrieveMode", CacheRetrieveMode.USE);
            properties.put("javax.persistence.storeMode", CacheStoreMode.USE);

            Board board = em.find(Board.class, 1L);
            em.refresh(board, properties);
        });
        Cache cache = emf.getCache();
        assertThat(cache.contains(Board.class, 1L)).isTrue();
    }

    @Test
    void testQueryCache() {
        doTransaction(em -> {
            Board board = new Board("title1");
            em.persist(board);
        });

        doTransaction(em -> {
            em.createQuery("select b from Board b", Board.class)
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
            /*
             * select
                    board0_.id as id1_1_,
                    board0_.title as title2_1_,
                    board0_.version as version3_1_
                from
                    board board0_
             */
        });

        doTransaction(em -> {
            em.createQuery("select b from Board b", Board.class)
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
            // 쿼리 실행 X
        });

        /*
         * 쿼리 캐시와 컬렉션 캐시의 주의사항
         * 쿼리 캐시와 컬렉션 캐시는 결과집합의 식별자 값만 캐시를 한다.
         * 따라서 캐시를 조회(히트)하면 각 식별자를 가지고 엔티티 캐시에서 조회하고 없다면 하나씩 DB에 쿼리를 날린다.
         * 결과집합이 100건이면 100번 sql을 조회하는 상황이 되니 성능이 나빠진다.
         * 따라서 컬렉션 캐시나 쿼리 캐시를 활용하고자 하면 대상 엔티티도 캐시를 해야한다.
         */

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
