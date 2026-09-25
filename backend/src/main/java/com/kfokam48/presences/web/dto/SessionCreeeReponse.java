package com.kfokam48.presences.web.dto;

import java.time.OffsetDateTime;

/**
 * Reponse de POST /api/sessions (contrat impose) : id, code, ouvertureAt,
 * expirationAt = ouvertureAt + 15 min (RG1). Dates ISO 8601 avec fuseau (ENF5).
 */
public record SessionCreeeReponse(
        Long id,
        String code,
        OffsetDateTime ouvertureAt,
        OffsetDateTime expirationAt
) {
}
