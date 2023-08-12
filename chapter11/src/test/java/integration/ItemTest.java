package integration;

import me.oopty.chapter11.domain.Sample;
import me.oopty.chapter11.service.SampleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:appConfig.xml")
@Transactional
public class ItemTest {

    @Autowired
    SampleService sampleService;

    @PersistenceContext
    EntityManager em;


    @Test
    public void 샘플생성() {
        Sample sample = new Sample("sample1");
        sampleService.create(sample);

        Sample result = em.find(Sample.class, 1L);
        assertEquals(result.getName(), "sample1");
    }


    @Test
    public void 샘플조회() {
        Sample sample = new Sample("sample1");
        em.persist(sample);

        Sample result = sampleService.findOne(1L);
        assertEquals(result.getName(), "sample1");
    }

    @Test
    public void 샘플삭제() {
        Sample sample = new Sample("sample1");
        em.persist(sample);

        Sample result = sampleService.findOne(1L);
        assertEquals(result.getName(), "sample1");

        sampleService.delete(1L);

        Sample result2 = sampleService.findOne(1L);
        assertNull(result2);
    }

    @Test
    public void 샘플수정() {
        Sample sample = new Sample("sample1");
        em.persist(sample);

        sampleService.update(new Sample(1L, "sample2"));

        Sample result = em.find(Sample.class, 1L);
        assertEquals("바뀐 속성이 저장되어야 한다.", "sample2", result.getName());
    }
}
