package com.kfokam48.presences.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.kfokam48.presences.domain.RegleMetierException;
import org.springframework.http.HttpStatus;

/**
 * RG13 : la note est un entier. Une valeur decimale (ex. 12.5) ne doit jamais
 * etre tronquee silencieusement en 12 (section 8 du cahier des charges) : elle
 * est refusee des la deserialisation par 400 NOTE_INVALIDE.
 */
public final class NoteEntiere {

    private NoteEntiere() {
    }

    /**
     * Convertit un noeud JSON en Integer strict : renvoie null si absent/null,
     * la valeur si entier, et leve NOTE_INVALIDE sinon (decimale, texte...).
     */
    public static Integer extraire(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (!node.isNumber() || !node.isIntegralNumber()) {
            throw new RegleMetierException("NOTE_INVALIDE", HttpStatus.BAD_REQUEST,
                    "La note doit être un entier entre 0 et 20.");
        }
        return node.intValue();
    }
}
