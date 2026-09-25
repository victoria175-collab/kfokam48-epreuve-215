package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.service.ExerciceService;
import com.kfokam48.presences.web.dto.ExerciceDeposeReponse;
import com.kfokam48.presences.web.dto.ExerciceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF3 : l'étudiant dépose le lien de son exercice. Corps et codes imposés par
 * le contrat (B2) : 201 nominal, 400 LIEN_INVALIDE, 403 ETUDIANT_HORS_PROMOTION,
 * 409 EXERCICE_DEJA_DEPOSE / SESSION_CLOTUREE.
 */
@RestController
public class ExerciceController {

    private final ExerciceService exerciceService;

    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping("/api/exercices")
    public ResponseEntity<ExerciceDeposeReponse> deposer(@Valid @RequestBody ExerciceRequest requete) {
        Exercice exercice = exerciceService.deposer(
                requete.sessionId(), requete.etudiantId(), requete.lien());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ExerciceDeposeReponse(exercice.getId(), exercice.getStatut().name()));
    }
}
