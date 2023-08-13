package me.oopty.chapter13.web;

import me.oopty.chapter13.domain.Sample;
import me.oopty.chapter13.domain.Tample;
import me.oopty.chapter13.service.SampleService;
import me.oopty.chapter13.service.TampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class SampleController {
    @Autowired
    private SampleService sampleService;
    @Autowired
    private TampleService tampleService;

    @RequestMapping(value = "/samples/{id}", method = RequestMethod.GET)
    public String get(@PathVariable("id") Long sampleId) {
        Sample sample = sampleService.findOne(sampleId);
        return "ok";
    }

    @RequestMapping(value = "/samples2/{id}", method = RequestMethod.GET)
    public String get2(@PathVariable("id") Long sampleId) {
        Sample sample = sampleService.findOne(sampleId);
        for(Tample tample : sample.getTamples()) {
            System.out.println(tample.getId());
        }
        return "ok";
    }

    @RequestMapping(value = "/samples", method = RequestMethod.POST)
    public String create(Sample sample) {
        sampleService.create(sample);
        return "ok";
    }

    @RequestMapping(value = "/samples2", method = RequestMethod.POST)
    public String create2(Sample sample) {
        Sample result = sampleService.create(sample);
        tampleService.associate(result);
        return "ok";
    }

    @RequestMapping(value = "/samples", method = RequestMethod.PUT)
    public String update(Sample sample) {
        sampleService.update(sample);
        return "ok";
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
        System.out.println(sample);
    }
    // PageableHandlerMethodArgumentResolver
    // SortHandlerMethodArgumentResolver
    @RequestMapping(value = "/test2")
    public void setPageable(Pageable pageable) {
        System.out.println(pageable);
    }
}
