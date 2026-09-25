package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionIdAndAuteurId(Long sessionId, Long auteurId);
}
