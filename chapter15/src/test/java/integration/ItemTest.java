package integration;

import me.oopty.chapter15.domain.Book;
import me.oopty.chapter15.domain.Item;
import me.oopty.chapter15.domain.Sample;
import me.oopty.chapter15.repository.SampleRepository;
import me.oopty.chapter15.service.SampleService;
import me.oopty.chapter15.visitor.PrintVisitor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.hibernate.proxy.HibernateProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:appConfig.xml")
@Transactional
public class ItemTest {

    @Autowired
    SampleService sampleService;

    @Autowired
    SampleRepository sampleRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntityNotFoundException() {
        Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                sampleRepository.findByName2("feji");
            }
        }).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    public void testProxyEquality() {
        Sample sample = new Sample("test");
        em.persist(sample);
        em.flush();
        em.clear();

        Sample refSample = em.getReference(Sample.class, sample.getId());
        Sample findedSample = em.find(Sample.class, sample.getId());


        assertTrue(refSample == findedSample);
        assertThat(refSample).isEqualTo(findedSample); // equals 비교
        assertTrue(refSample.getId().equals(findedSample.getId()));

        System.out.println("refSample.getClass() = " + refSample.getClass());
        System.out.println("findedSample.getClass()" + findedSample.getClass());
        /*
            refSample.getClass() = class me.oopty.chapter15.domain.Sample_$$_jvst5c7_1
            findedSample.getClass()class me.oopty.chapter15.domain.Sample_$$_jvst5c7_1
         */
    }

    @Test
    public void testProxyInstanceOf() {
        Sample sample = new Sample("test");
        em.persist(sample);
        em.flush();
        em.clear();

        Sample refSample = em.getReference(Sample.class, sample.getId());

        assertTrue(refSample instanceof Sample);
    }

    @Test
    public void testInheritanceProxy() {
        Book book = new Book();
        em.persist(book);

        em.flush();
        em.clear();

        Item refItem = em.getReference(Item.class, book.getId());

        assertFalse(refItem instanceof Book);
        assertTrue(refItem instanceof Item);
        // 해결방법
        // 1. JPQL로 직접 조회
        // 2. 프록시 벗기기
        // 3. 기능을 위한 별도의 인터페이스 제공
        // 4. 비지터 패턴 사용
    }

    @Test
    public void 프록시_벗기기() {
        Book book = new Book();
        book.setName("book1");
        em.persist(book);

        em.flush();
        em.clear();

        Item refItem = em.getReference(Item.class, book.getId());

        Book result = unProxy(refItem);
        assertThat(result.getName()).isEqualTo("book1");
    }

    private static <T> T unProxy(Object entity) {
        if(entity instanceof HibernateProxy) {
            entity = ((HibernateProxy) entity)
                    .getHibernateLazyInitializer()
                    .getImplementation();
        }
        return (T) entity;
    }

    @Test
    public void 기능을_위한_별도의_인터페이스_제공() {
        Book book = new Book();
        book.setName("book1");
        book.setAuthor("author1");
        em.persist(book);

        em.flush();
        em.clear();

        Item refItem = em.getReference(Item.class, book.getId());

        assertThat(refItem.getAuthor()).isEqualTo("author1");
    }

    @Test
    public void 비지터_패턴_사용() {
        Book book = new Book();
        book.setName("book1");
        book.setAuthor("author1");
        em.persist(book);

        em.flush();
        em.clear();

        Item refItem = em.getReference(Item.class, book.getId());

        refItem.accept(new PrintVisitor()); // print

        assertThat(refItem.getAuthor()).isEqualTo("author1");
    }

    /* 성능 최적화
     * 1. N+1 문제
     *  - fetch join으로 해결
     *  - batchSize 설정으로 해결
     *  - FetchMode를 SUBSELECT로 해결
     * 2. 트랜젝션 설정으로 성능 최적화
     *  - 트렌젝션을 읽기 전용으로 생성하면 flush 발생하지 않아서 성능 좋아짐 @Transactional(readOnly = true)
     *  - 트랜젝션을 만들지 않으면 성능 좋아짐 @Transactional(propagation = Propagation.NOT_SUPPORTED)
     *  - 스칼라 타입으로 조회하거나, 읽기 전용 쿼리 힌트를 사용하면 스냅샷을 만들지 않아 메모리를 아낄 수 있음 `org.hibernate.readOnly`
     */
}
