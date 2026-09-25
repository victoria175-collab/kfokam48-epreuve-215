package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Etudiant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    List<Etudiant> findByPromotionIdOrderById(Long promotionId);
}
