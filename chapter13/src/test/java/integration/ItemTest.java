package integration;

import me.oopty.chapter13.domain.Sample;
import me.oopty.chapter13.repository.SampleRepository;
import me.oopty.chapter13.service.SampleService;
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

    @Autowired
    SampleRepository sampleRepository;

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

    @Test
    public void 이름으로_찾기() {
        Sample sample = new Sample( "sample1");
        em.persist(sample);

        Sample result = sampleRepository.findByName("sample1");
        assertNotNull(result);
    }

    @Test
    public void 이름으로_찾기2() {
        Sample sample = new Sample( "sample1");
        em.persist(sample);

        Sample result = sampleRepository.findByName2("sample1");
        assertNotNull(result);
    }

    @Test
    public void 이름으로_찾기3() {
        Sample sample = new Sample( "sample1");
        em.persist(sample);

        Sample result = sampleRepository.contains("samp");
        assertNotNull(result);
    }
}
