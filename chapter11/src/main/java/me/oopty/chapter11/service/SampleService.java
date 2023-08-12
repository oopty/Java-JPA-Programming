package me.oopty.chapter11.service;

import me.oopty.chapter11.domain.Sample;
import me.oopty.chapter11.repository.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SampleService {
    @Autowired
    private SampleRepository sampleRepository;

    public Sample findOne(Long sampleId) {
        return sampleRepository.findOne(sampleId);
    }

    public Sample create(Sample sample) {
        return sampleRepository.save(sample);
    }

    public Sample update(Sample sample) {
        return sampleRepository.save(sample);
    }

    public Sample delete(Long sampleId) {
        return sampleRepository.delete(sampleId);
    }
}
