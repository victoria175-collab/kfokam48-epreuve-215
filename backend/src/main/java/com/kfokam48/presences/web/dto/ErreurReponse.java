package com.kfokam48.presences.web.dto;

/**
 * Format d'erreur imposé par le contrat d'API pour TOUTES les erreurs, sans
 * exception (B2, ENF4). Une stack trace, un corps vide ou la page d'erreur
 * par défaut de Spring valent zéro sur ce critère.
 */
public record ErreurReponse(String code, String message) {
}
