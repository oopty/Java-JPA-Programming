package me.oopty.chapter15.repository;

import me.oopty.chapter15.domain.Sample;

public interface CustomSampleRepository {
    Sample findByName2(String name);

    Sample contains(String name);
}
