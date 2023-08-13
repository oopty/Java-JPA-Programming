package me.oopty.chapter12.repository;

import me.oopty.chapter12.domain.Sample;

public interface CustomSampleRepository {
    Sample findByName2(String name);

    Sample contains(String name);
}
