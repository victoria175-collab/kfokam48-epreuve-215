package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.service.SessionService;
import com.kfokam48.presences.web.dto.SessionCreationRequest;
import com.kfokam48.presences.web.dto.SessionCreeeReponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF1 : le formateur ouvre une session et obtient un code de presence.
 * Chemin, corps et codes de statut imposés par le contrat d'API (B2) :
 * 201 en nominal, 400 si le titre ou la promotion manque, 404 PROMOTION_INCONNUE.
 */
@RestController
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/api/sessions")
    public ResponseEntity<SessionCreeeReponse> ouvrir(@Valid @RequestBody SessionCreationRequest requete) {
        SessionCours session = sessionService.ouvrir(requete.titre(), requete.promotionId());
        SessionCreeeReponse reponse = new SessionCreeeReponse(
                session.getId(), session.getCode(), session.getOuvertureAt(), session.getExpirationAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
