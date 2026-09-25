package com.kfokam48.presences.web.dto;

/**
 * Reponse de GET /api/promotions (EF8) : les listes des ecrans ne contiennent
 * jamais le code de presence (decision Z3).
 */
public record PromotionReponse(Long id, String nom) {
}
