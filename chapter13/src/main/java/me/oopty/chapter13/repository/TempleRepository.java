package me.oopty.chapter13.repository;

import me.oopty.chapter13.domain.Tample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TempleRepository extends JpaRepository<Tample, Long> {
}
