package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corps de POST /api/presences (contrat impose) : code et etudiantId requis.
 */
public record PresenceRequest(

        @NotBlank(message = "le code de présence est obligatoire")
        String code,

        @NotNull(message = "l'étudiant est obligatoire")
        Long etudiantId
) {
}
