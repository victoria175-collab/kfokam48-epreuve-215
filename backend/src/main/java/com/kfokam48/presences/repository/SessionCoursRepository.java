package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.SessionCours;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionCoursRepository extends JpaRepository<SessionCours, Long> {

    boolean existsByCode(String code);

    Optional<SessionCours> findByCode(String code);

    List<SessionCours> findByPromotionIdOrderByOuvertureAtDesc(Long promotionId);
}
