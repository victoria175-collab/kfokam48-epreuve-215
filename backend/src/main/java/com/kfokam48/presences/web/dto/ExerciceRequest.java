package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corps de POST /api/exercices (contrat impose) : sessionId, etudiantId et lien
 * requis. La validite du lien (RG7) est verifiee par le service.
 */
public record ExerciceRequest(

        @NotNull(message = "la session est obligatoire")
        Long sessionId,

        @NotNull(message = "l'étudiant est obligatoire")
        Long etudiantId,

        @NotBlank(message = "le lien de l'exercice est obligatoire")
        @Size(max = 2048, message = "le lien dépasse 2048 caractères")
        String lien
) {
}
