package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.TableauRepository;
import com.kfokam48.presences.web.dto.TableauLigneReponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF7 : le tableau recapitulatif du formateur. Contrat impose : 200 avec les
 * compteurs par etudiant, 404 PROMOTION_INCONNUE. La moyenne vient de l'API
 * (RG17) ; le frontend ne la recalcule jamais (F3).
 */
@RestController
public class TableauController {

    private final TableauRepository tableau;
    private final PromotionRepository promotions;

    public TableauController(TableauRepository tableau, PromotionRepository promotions) {
        this.tableau = tableau;
        this.promotions = promotions;
    }

    @GetMapping("/api/tableau")
    public List<TableauLigneReponse> tableau(@RequestParam Long promotionId) {
        Promotion promotion = promotions.findById(promotionId)
                .orElseThrow(() -> new RegleMetierException("PROMOTION_INCONNUE", HttpStatus.NOT_FOUND,
                        "La promotion demandée n'existe pas."));
        return tableau.tableauDeLaPromotion(promotion.getId());
    }
}
