package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import com.kfokam48.presences.web.dto.ExerciceEtudiantReponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EF12 mise a jour par l'issue #34 : l'etudiant consulte ses exercices avec,
 * pour chacun, son statut, la note retenue et le commentaire. RG16 / Z14 :
 * aucune reponse ne contient l'identite d'un relecteur. La note retenue est la
 * moyenne des relectures rendues ; si une seule des deux est rendue, sa note
 * est fournie marquee comme provisoire.
 */
@Service
public class NotesEtudiantService {

    private final EtudiantRepository etudiants;
    private final ExerciceRepository exercices;
    private final RelectureRepository relectures;

    public NotesEtudiantService(EtudiantRepository etudiants,
                                ExerciceRepository exercices,
                                RelectureRepository relectures) {
        this.etudiants = etudiants;
        this.exercices = exercices;
        this.relectures = relectures;
    }

    @Transactional(readOnly = true)
    public List<ExerciceEtudiantReponse> exercicesDe(Long etudiantId) {
        Etudiant etudiant = etudiants.findById(etudiantId)
                .orElseThrow(() -> new RegleMetierException("ETUDIANT_INCONNU", HttpStatus.NOT_FOUND,
                        "L'étudiant demandé n'existe pas."));

        return exercices.findByAuteurIdOrderById(etudiant.getId()).stream()
                .map(exercice -> {
                    List<Relecture> rendues =
                            relectures.findByExerciceIdAndRendueAtIsNotNull(exercice.getId());

                    BigDecimal note = null;
                    boolean provisoire = false;
                    String commentaire = null;
                    if (rendues.size() == 1) {
                        note = BigDecimal.valueOf(rendues.get(0).getNote());
                        provisoire = true;
                        commentaire = rendues.get(0).getCommentaire();
                    } else if (rendues.size() >= 2) {
                        int somme = 0;
                        StringBuilder commentaires = new StringBuilder();
                        for (Relecture r : rendues) {
                            somme += r.getNote();
                            if (commentaires.length() > 0) {
                                commentaires.append(" ; ");
                            }
                            commentaires.append(r.getCommentaire() == null ? "" : r.getCommentaire());
                        }
                        note = BigDecimal.valueOf(somme)
                                .divide(BigDecimal.valueOf(rendues.size()), 2, RoundingMode.HALF_UP);
                        commentaire = commentaires.toString();
                    }

                    return new ExerciceEtudiantReponse(
                            exercice.getId(),
                            exercice.getSession().getId(),
                            exercice.getSession().getTitre(),
                            exercice.getStatut().name(),
                            exercice.getLien(),
                            note,
                            provisoire,
                            commentaire);
                })
                .toList();
    }
}
