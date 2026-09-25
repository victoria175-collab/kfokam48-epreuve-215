package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Presence;
import com.kfokam48.presences.service.PresenceService;
import com.kfokam48.presences.web.dto.PresenceReponse;
import com.kfokam48.presences.web.dto.PresenceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF2 : l'étudiant marque sa présence avec le code. Corps et codes de statut
 * imposés par le contrat (B2) : 201 nominal, 400 CODE_INCONNU, 409
 * DEJA_PRESENT, 410 CODE_EXPIRE.
 */
@RestController
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping("/api/presences")
    public ResponseEntity<PresenceReponse> marquer(@Valid @RequestBody PresenceRequest requete) {
        Presence presence = presenceService.marquer(requete.code(), requete.etudiantId());
        PresenceReponse reponse = new PresenceReponse(
                presence.getId(),
                presence.getSession().getId(),
                presence.getEtudiant().getId(),
                presence.getSource().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
