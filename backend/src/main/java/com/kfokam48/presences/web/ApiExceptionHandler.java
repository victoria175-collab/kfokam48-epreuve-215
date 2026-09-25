package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.web.dto.ErreurReponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * B4, ENF4 : gestion centralisée des erreurs. Toute erreur — règle métier,
 * validation, corps JSON mal formé, route inexistante, erreur interne — est
 * renvoyée au format imposé { code, message }, en français, sans stack trace.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ErreurReponse> regleMetier(RegleMetierException e) {
        return ResponseEntity.status(e.getStatut())
                .body(new ErreurReponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurReponse> validation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .findFirst()
                .orElse("Donnée invalide.");
        return ResponseEntity.badRequest()
                .body(new ErreurReponse("DONNEE_INVALIDE", detail));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurReponse> corpsIllisible(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErreurReponse("CORPS_JSON_INVALIDE",
                        "Le corps de la requête n'est pas un JSON exploitable."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurReponse> parametreInvalide(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(new ErreurReponse("PARAMETRE_INVALIDE",
                        "Le paramètre " + e.getName() + " n'a pas le type attendu."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurReponse> routeInconnue(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurReponse("ROUTE_INCONNUE",
                        "La ressource demandée n'existe pas."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurReponse> erreurInattendue(Exception e) {
        // Aucune stack trace vers le client (B4) ; le détail reste dans les logs.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErreurReponse("ERREUR_INTERNE",
                        "Une erreur interne est survenue."));
    }
}
