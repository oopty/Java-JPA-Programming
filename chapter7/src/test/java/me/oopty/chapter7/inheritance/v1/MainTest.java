package me.oopty.chapter7.inheritance.v1;

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
    void insert() {

        doTransaction(em -> {
            Album album = new Album();
            Book book = new Book();
            Movie movie = new Movie();

            album.setId(1L);
            album.setName("album1");
            album.setAuthor("oopty");
            album.setPrice(1000);

            book.setId(2L);
            book.setName("book1");
            book.setPrice(1000);

            movie.setId(3L);
            movie.setName("movie1");
            movie.setActor("oopty");
            movie.setDirector("oopty");
            movie.setPrice(1000);

            em.persist(album);
            /* insert 2번 일어남
             1. insert into ITEM (name, price, DTYPE, ITEM_ID) values (?, ?, 'A', ?)
             2. insert into ALBUM (author, ITEM_ID) values (?, ?)
            */
            em.persist(book);
            em.persist(movie);
        });
    }

    @Test
    void select() {

        doTransaction(em -> {
            Album album = new Album();
            Book book = new Book();
            Movie movie = new Movie();

            album.setId(1L);
            album.setName("album1");
            album.setAuthor("oopty");
            album.setPrice(1000);

            book.setId(2L);
            book.setName("book1");
            book.setPrice(1000);

            movie.setId(3L);
            movie.setName("movie1");
            movie.setActor("oopty");
            movie.setDirector("oopty");
            movie.setPrice(1000);

            em.persist(album);
            em.persist(book);
            em.persist(movie);
        });

        doTransaction(em -> {
            Album album = em.find(Album.class, 1L);
            /*
             select
                album0_.ITEM_ID as item_id2_2_0_,
                album0_1_.name as name3_2_0_,
                album0_1_.price as price4_2_0_,
                album0_.author as author1_0_0_
            from
                ALBUM album0_
            inner join
                ITEM album0_1_
                    on album0_.ITEM_ID=album0_1_.ITEM_ID
            where
                album0_.ITEM_ID=?
             */
            Book book = em.find(Book.class, 2L);
            Movie movie = em.find(Movie.class, 3L);
        });
    }

    @Test
    void update() {

        doTransaction(em -> {
            Album album = new Album();
            Book book = new Book();
            Movie movie = new Movie();

            album.setId(1L);
            album.setName("album1");
            album.setAuthor("oopty");
            album.setPrice(1000);

            book.setId(2L);
            book.setName("book1");
            book.setPrice(1000);

            movie.setId(3L);
            movie.setName("movie1");
            movie.setActor("oopty");
            movie.setDirector("oopty");
            movie.setPrice(1000);

            em.persist(album);
            em.persist(book);
            em.persist(movie);
        });

        doTransaction(em -> {
            Album album = em.find(Album.class, 1L);
            album.setName("ytpoo");
            album.setAuthor("ytpoo");
        });
        /*
         1. update ITEM set name=?, price=? where ITEM_ID=?
         2. update ALBUM set author=? where ITEM_ID=?
         */
    }

    @Test
    void delete() {

        doTransaction(em -> {
            Album album = new Album();
            Book book = new Book();
            Movie movie = new Movie();

            album.setId(1L);
            album.setName("album1");
            album.setAuthor("oopty");
            album.setPrice(1000);

            book.setId(2L);
            book.setName("book1");
            book.setPrice(1000);

            movie.setId(3L);
            movie.setName("movie1");
            movie.setActor("oopty");
            movie.setDirector("oopty");
            movie.setPrice(1000);

            em.persist(album);
            em.persist(book);
            em.persist(movie);
        });

        doTransaction(em -> {
            Album album = em.find(Album.class, 1L);
            em.remove(album);
        });
        /*
         1. delete from ALBUM where ITEM_ID=?
         2. delete from ITEM where ITEM_ID=?
         */
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
