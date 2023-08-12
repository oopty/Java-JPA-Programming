package me.oopty.chapter12.web;

import me.oopty.chapter12.domain.Sample;
import me.oopty.chapter12.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
public class SampleController {
    @Autowired
    private SampleService sampleService;

    @Autowired
    private SpringDataWebConfiguration springDataWebConfiguration;

    @RequestMapping(value = "/samples/{id}", method = RequestMethod.GET)
    public Sample get(@PathVariable("id") Long sampleId) {
        return sampleService.findOne(sampleId);
    }

    @RequestMapping(value = "/samples", method = RequestMethod.POST)
    public Sample create(Sample sample) {
        return sampleService.create(sample);
    }

    @RequestMapping(value = "/samples", method = RequestMethod.PUT)
    public Sample update(Sample sample) {
        return sampleService.update(sample);
    }

    @RequestMapping(value = "/samples/{id}", method = RequestMethod.DELETE)
    public Sample delete(@PathVariable("id") Long sampleId) {
        return sampleService.delete(sampleId);
    }

    /**
     * 아래는 web 확장기능을 테스트 하기 위한 것
     */

    // DomainClassConverter
    @RequestMapping(value = "/test")
    public void domainConverter(@RequestParam("id") Sample sample) {
        System.out.println(springDataWebConfiguration);
        System.out.println(sample);
    }
    // PageableHandlerMethodArgumentResolver
    // SortHandlerMethodArgumentResolver
    @RequestMapping(value = "/test2")
    public void setPageable(Pageable pageable) {
        System.out.println(pageable);
    }
}
