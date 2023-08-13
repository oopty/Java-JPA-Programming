package me.oopty.chapter13.repository;

import me.oopty.chapter13.domain.Sample;

public interface CustomSampleRepository {
    Sample findByName2(String name);

    Sample contains(String name);
}
