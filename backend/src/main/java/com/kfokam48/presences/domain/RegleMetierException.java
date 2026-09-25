package com.kfokam48.presences.domain;

import org.springframework.http.HttpStatus;

/**
 * Exception métier : porte le code d'erreur du contrat (ex. CODE_EXPIRE) et le
 * statut HTTP à renvoyer. Les services la lèvent, le @RestControllerAdvice la
 * traduit au format imposé { code, message } (B2, B4).
 */
public class RegleMetierException extends RuntimeException {

    private final String code;
    private final HttpStatus statut;

    public RegleMetierException(String code, HttpStatus statut, String message) {
        super(message);
        this.code = code;
        this.statut = statut;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatut() {
        return statut;
    }
}
