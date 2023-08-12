package integration;

import me.oopty.chapter11.domain.Sample;
import me.oopty.chapter11.service.SampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        Sample response = sampleService.create(sample);

        Sample result = em.find(Sample.class, response.getId());
        assertEquals(result.getName(), "sample1");
    }


    @Test
    public void 샘플조회() {
        Sample sample = new Sample("sample1");
        em.persist(sample);

        Sample result = sampleService.findOne(sample.getId());
        assertEquals(result.getName(), "sample1");
    }

    @Test
    public void 샘플삭제() {
        Sample sample = new Sample( "sample1");
        em.persist(sample);

        Sample result = sampleService.findOne(sample.getId());
        assertEquals(result.getName(), "sample1");

        sampleService.delete(sample.getId());

        Sample result2 = sampleService.findOne(sample.getId());
        assertNull(result2);
    }

    @Test
    public void 샘플수정() {
        Sample sample = new Sample( "sample1");
        em.persist(sample);

        sampleService.update(new Sample(sample.getId(), "sample2"));

        Sample result = em.find(Sample.class, sample.getId());
        assertEquals("바뀐 속성이 저장되어야 한다.", "sample2", result.getName());
    }
}
