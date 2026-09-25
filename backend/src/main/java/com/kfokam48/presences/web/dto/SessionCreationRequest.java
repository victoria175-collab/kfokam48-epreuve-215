package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corps de POST /api/sessions (contrat impose) : titre et promotionId requis.
 */
public record SessionCreationRequest(

        @NotBlank(message = "le titre est obligatoire")
        String titre,

        @NotNull(message = "la promotion est obligatoire")
        Long promotionId
) {
}
