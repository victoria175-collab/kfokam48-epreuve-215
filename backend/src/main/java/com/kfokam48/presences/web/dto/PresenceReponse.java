package com.kfokam48.presences.web.dto;

/**
 * Reponse de POST /api/presences (contrat impose) : id, sessionId, etudiantId
 * et source (ETUDIANT pour le marquage par code, EF2).
 */
public record PresenceReponse(
        Long id,
        Long sessionId,
        Long etudiantId,
        String source
) {
}
