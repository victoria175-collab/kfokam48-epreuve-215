package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.web.dto.EtudiantReponse;
import com.kfokam48.presences.web.dto.PromotionReponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF8 : les ecrans choisissent promotion et etudiant dans des listes fournies
 * par l'API, sans saisie manuelle d'identifiant. Referentiels en lecture seule
 * (section 3 du cahier des charges, decision Z4).
 */
@RestController
public class ReferentielController {

    private final PromotionRepository promotions;
    private final EtudiantRepository etudiants;

    public ReferentielController(PromotionRepository promotions, EtudiantRepository etudiants) {
        this.promotions = promotions;
        this.etudiants = etudiants;
    }

    @GetMapping("/api/promotions")
    public List<PromotionReponse> promotions() {
        return promotions.findAll().stream()
                .map(p -> new PromotionReponse(p.getId(), p.getNom()))
                .toList();
    }

    @GetMapping("/api/promotions/{id}/etudiants")
    public List<EtudiantReponse> etudiants(@PathVariable Long id) {
        Promotion promotion = promotions.findById(id)
                .orElseThrow(() -> new RegleMetierException("PROMOTION_INCONNUE", HttpStatus.NOT_FOUND,
                        "La promotion demandée n'existe pas."));
        return etudiants.findByPromotionIdOrderById(promotion.getId()).stream()
                .map(e -> new EtudiantReponse(e.getId(), e.getNom()))
                .toList();
    }
}
