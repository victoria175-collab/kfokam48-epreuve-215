package com.kfokam48.presences.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.service.RelectureService;
import com.kfokam48.presences.web.dto.RelectureListeReponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF5 et EF6 : consultation et rendu des relectures. Chemins, corps et codes
 * imposes par le contrat (B2) : 200 au rendu, 400 NOTE_INVALIDE (y compris une
 * note decimale, RG13), 403 AUTO_RELECTURE / RELECTEUR_NON_AFFECTE (Z11), 409
 * RELECTURE_DEJA_RENDUE (RG14) / SESSION_CLOTUREE (RG15).
 */
@RestController
public class RelectureController {

    private final RelectureService relectureService;
    private final ObjectMapper objectMapper;

    public RelectureController(RelectureService relectureService, ObjectMapper objectMapper) {
        this.relectureService = relectureService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/relectures")
    public List<RelectureListeReponse> duRelecteur(@RequestParam Long relecteurId) {
        return relectureService.duRelecteur(relecteurId).stream()
                .map(r -> new RelectureListeReponse(
                        r.getId(),
                        r.getExercice().getId(),
                        r.getExercice().getLien(),
                        r.getExercice().getSession().getId(),
                        r.getExercice().getSession().getTitre(),
                        r.getRendueAt() != null,
                        r.getRendueAt()))
                .toList();
    }

    @PostMapping("/api/relectures/{id}")
    public ResponseEntity<Void> rendre(@PathVariable Long id,
                                       @RequestBody Map<String, Object> corpsBrut,
                                       @RequestHeader(value = "X-Etudiant-Id", required = false)
                                       Long etudiantDeclencheur) {
        // RG13 : la note est lue strictement — decimale ou texte => NOTE_INVALIDE.
        JsonNode noeudNote = null;
        String commentaire = null;
        Integer note = null;
        try {
            JsonNode corps = objectMapper.valueToTree(corpsBrut);
            noeudNote = corps.get("note");
            JsonNode noeudCommentaire = corps.get("commentaire");
            commentaire = noeudCommentaire == null || noeudCommentaire.isNull()
                    ? null : noeudCommentaire.asText();
            note = NoteEntiere.extraire(noeudNote);
        } catch (RegleMetierException e) {
            throw e;
        }
        relectureService.rendre(id, note, commentaire, etudiantDeclencheur);
        return ResponseEntity.ok().build();
    }
}
