package me.oopty.chapter13.repository;

import me.oopty.chapter13.domain.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long>, CustomSampleRepository {

    Sample findByName(String name);
}
