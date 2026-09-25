package com.kfokam48.presences.web.dto;

import java.time.OffsetDateTime;

/**
 * Reponse de GET /api/relectures (EF5, contrat) : relectures d'un relecteur
 * avec le lien de l'exercice, la session et l'etat. RG16 / Z14 : aucune
 * reponse destinee a l'etudiant ne contient le nom de l'auteur ni relecteurId.
 */
public record RelectureListeReponse(
        Long id,
        Long exerciceId,
        String lienExercice,
        Long sessionId,
        String sessionTitre,
        boolean rendue,
        OffsetDateTime rendueAt
) {
}
