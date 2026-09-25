package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import com.kfokam48.presences.service.SessionService;
import com.kfokam48.presences.web.dto.SessionCreationRequest;
import com.kfokam48.presences.web.dto.SessionCreeeReponse;
import com.kfokam48.presences.web.dto.SessionListeReponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF1 et EF8 : ouverture de session avec code de presence, et liste des
 * sessions d'une promotion pour alimenter les ecrans. Codes de statut et
 * corps imposes par le contrat (B2) ; la liste ne contient pas le code de
 * presence, expose uniquement dans le detail d'une session (decision Z3).
 */
@RestController
public class SessionController {

    private final SessionService sessionService;
    private final PromotionRepository promotions;
    private final SessionCoursRepository sessions;

    public SessionController(SessionService sessionService,
                             PromotionRepository promotions,
                             SessionCoursRepository sessions) {
        this.sessionService = sessionService;
        this.promotions = promotions;
        this.sessions = sessions;
    }

    @PostMapping("/api/sessions")
    public ResponseEntity<SessionCreeeReponse> ouvrir(@Valid @RequestBody SessionCreationRequest requete) {
        SessionCours session = sessionService.ouvrir(requete.titre(), requete.promotionId());
        SessionCreeeReponse reponse = new SessionCreeeReponse(
                session.getId(), session.getCode(), session.getOuvertureAt(), session.getExpirationAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @GetMapping("/api/sessions")
    public List<SessionListeReponse> lister(@RequestParam Long promotionId) {
        Promotion promotion = promotions.findById(promotionId)
                .orElseThrow(() -> new RegleMetierException("PROMOTION_INCONNUE", HttpStatus.NOT_FOUND,
                        "La promotion demandée n'existe pas."));
        return sessions.findByPromotionIdOrderByOuvertureAtDesc(promotion.getId()).stream()
                .map(s -> new SessionListeReponse(s.getId(), s.getTitre(),
                        s.getOuvertureAt(), s.getExpirationAt(), s.getClotureeAt()))
                .toList();
    }
}
