package com.kfokam48.presences.web.dto;

import java.time.OffsetDateTime;

/**
 * Reponse de GET /api/sessions?promotionId= (contrat impose) : la liste des
 * sessions ne contient pas le code ; celui-ci n'est expose que dans le detail
 * d'une session (GET /api/sessions/{id}, decision Z3).
 */
public record SessionListeReponse(
        Long id,
        String titre,
        OffsetDateTime ouvertureAt,
        OffsetDateTime expirationAt,
        OffsetDateTime clotureeAt
) {
}
