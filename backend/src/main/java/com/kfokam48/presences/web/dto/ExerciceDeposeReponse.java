package com.kfokam48.presences.web.dto;

/**
 * Reponse de POST /api/exercices (contrat impose) : identifiant et statut du
 * nouvel exercice (DEPOSE ou EN_ATTENTE_RELECTURE apres affectation, EF4).
 */
public record ExerciceDeposeReponse(Long id, String statut) {
}
