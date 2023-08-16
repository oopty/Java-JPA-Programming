package me.oopty.chapter15.repository;

import me.oopty.chapter15.domain.Tample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempleRepository extends JpaRepository<Tample, Long> {
}
