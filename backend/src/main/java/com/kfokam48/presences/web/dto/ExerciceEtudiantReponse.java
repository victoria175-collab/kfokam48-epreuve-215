package com.kfokam48.presences.web.dto;

import java.math.BigDecimal;

/**
 * Reponse de GET /api/etudiants/{id}/exercices (EF12, contrat mis a jour par
 * l'issue #34). RG16 / Z14 : aucune identite de relecteur. Issue #34 : la note
 * retenue est la moyenne des relectures rendues ; si une seule est rendue, sa
 * note est affichee avec noteProvisoire = true.
 */
public record ExerciceEtudiantReponse(
        Long id,
        Long sessionId,
        String sessionTitre,
        String statut,
        String lien,
        BigDecimal note,
        boolean noteProvisoire,
        String commentaire
) {
}
