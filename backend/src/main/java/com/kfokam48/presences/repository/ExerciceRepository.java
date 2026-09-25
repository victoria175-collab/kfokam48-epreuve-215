package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Exercice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    boolean existsBySessionIdAndAuteurId(Long sessionId, Long auteurId);

    List<Exercice> findBySessionIdAndStatut(Long sessionId, Exercice.Statut statut);

    /** Exercices d'un auteur, par ordre de dépôt (EF12). */
    List<Exercice> findByAuteurIdOrderById(Long auteurId);
}
