package me.oopty.chapter11.web;

import me.oopty.chapter11.domain.Sample;
import me.oopty.chapter11.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sample")
public class SampleController {
    @Autowired
    private SampleService sampleService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Sample get(@PathVariable("id") Long sampleId) {
        return sampleService.findOne(sampleId);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Sample create(@RequestBody Sample sample) {
        return sampleService.create(sample);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Sample update(@RequestBody Sample sample) {
        return sampleService.update(sample);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Sample delete(Long sampleId) {
        return sampleService.delete(sampleId);
    }

}
