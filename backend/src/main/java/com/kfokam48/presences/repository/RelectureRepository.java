package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Relecture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** Relectures non rendues d'un étudiant dans une session (RG11, charge). */
    List<Relecture> findByRelecteurIdAndRendueAtIsNull(Long relecteurId);

    /** Toutes les relectures affectees a un etudiant (EF5). */
    List<Relecture> findByRelecteurId(Long relecteurId);

    /** Toutes les relectures des exercices d'une session (charge RG11). */
    List<Relecture> findByExerciceSessionId(Long sessionId);

    boolean existsByExerciceId(Long exerciceId);

    /** Nombre d'affectations déjà posées pour l'exercice (0, 1 ou 2 — issue #34). */
    long countByExerciceId(Long exerciceId);

    /** Toutes les affectations de l'exercice (issue #34). */
    List<Relecture> findByExerciceId(Long exerciceId);

    /** Relectures rendues de l'exercice (note provisoire vs définitive, issue #34). */
    List<Relecture> findByExerciceIdAndRendueAtIsNotNull(Long exerciceId);
}
