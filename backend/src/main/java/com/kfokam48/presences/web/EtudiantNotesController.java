package com.kfokam48.presences.web;

import com.kfokam48.presences.service.NotesEtudiantService;
import com.kfokam48.presences.web.dto.ExerciceEtudiantReponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * EF12 (issue #15) mise a jour par l'issue #34 : l'etudiant consulte ses
 * exercices, avec la note retenue et son caractere provisoire. Le calcul est
 * porte par NotesEtudiantService (B3) ; aucune identite de relecteur (RG16).
 */
@RestController
public class EtudiantNotesController {

    private final NotesEtudiantService notes;

    public EtudiantNotesController(NotesEtudiantService notes) {
        this.notes = notes;
    }

    @GetMapping("/api/etudiants/{id}/exercices")
    public List<ExerciceEtudiantReponse> exercices(@PathVariable Long id) {
        return notes.exercicesDe(id);
    }
}
