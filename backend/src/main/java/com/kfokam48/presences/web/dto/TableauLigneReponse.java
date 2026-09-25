package com.kfokam48.presences.web.dto;

import java.math.BigDecimal;

/**
 * Une ligne du tableau recapitulatif (EF7, contrat impose). La moyenne est
 * calculee par l'API uniquement (RG17, contrainte F3), arrondie a deux
 * decimales, null si aucune note recue. presences est le nombre de sessions
 * auxquelles l'etudiant a ete present (contradiction C2).
 */
public record TableauLigneReponse(
        Long etudiantId,
        String nom,
        long presences,
        long exercicesDeposes,
        BigDecimal moyenne,
        long relecturesEnAttente
) {
}
