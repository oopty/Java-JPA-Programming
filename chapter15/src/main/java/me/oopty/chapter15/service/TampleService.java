package me.oopty.chapter15.service;

import me.oopty.chapter15.domain.Sample;
import me.oopty.chapter15.domain.Tample;
import me.oopty.chapter15.repository.TempleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TampleService {
    @Autowired
    private TempleRepository tampleRepository;

    public void associate(Sample sample) {
        Tample tample1 = new Tample("tample1", sample);
        Tample tample2 = new Tample("tample2", sample);
        tampleRepository.save(tample1);
        tampleRepository.save(tample2);
    }
}
