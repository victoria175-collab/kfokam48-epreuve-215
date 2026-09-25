package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EF5 : le relecteur consulte les relectures qui lui sont affectees, sans le
 * nom de l'auteur (RG16). EF6 : il rend une note et un commentaire, une seule
 * fois (RG14), jamais sur sa propre exercice (RG12), jamais apres la cloture
 * (RG15). L'identite declaree transite par l'en-tete X-Etudiant-Id (Z11).
 */
@Service
public class RelectureService {

    private final RelectureRepository relectures;
    private final ExerciceRepository exercices;
    private final EtudiantRepository etudiants;
    private final Clock clock;

    public RelectureService(RelectureRepository relectures,
                            ExerciceRepository exercices,
                            EtudiantRepository etudiants,
                            Clock clock) {
        this.relectures = relectures;
        this.exercices = exercices;
        this.etudiants = etudiants;
        this.clock = clock;
    }

    /** EF5 : relectures affectees a un etudiant, sans identite de l'auteur (RG16). */
    @Transactional(readOnly = true)
    public List<Relecture> duRelecteur(long relecteurId) {
        etudiants.findById(relecteurId)
                .orElseThrow(() -> new RegleMetierException("ETUDIANT_INCONNU", HttpStatus.BAD_REQUEST,
                        "L'étudiant indiqué n'existe pas."));
        return relectures.findByRelecteurId(relecteurId);
    }

    /** EF6 : rendu de la note et du commentaire, definitif (RG14). */
    @Transactional
    public Relecture rendre(long relectureId, Integer note, String commentaire, Long etudiantDeclencheur) {
        // RG13 : la note est un entier 0..20.
        if (note == null || note < 0 || note > 20) {
            throw new RegleMetierException("NOTE_INVALIDE", HttpStatus.BAD_REQUEST,
                    "La note doit être un entier entre 0 et 20.");
        }

        Relecture relecture = relectures.findById(relectureId)
                .orElseThrow(() -> new RegleMetierException("RELECTURE_INCONNUE", HttpStatus.NOT_FOUND,
                        "La relecture demandée n'existe pas."));

        // Z11 : si l'en-tete identifie un etudiant, on verifie son droit.
        if (etudiantDeclencheur != null) {
            if (etudiantDeclencheur.equals(relecture.getExercice().getAuteur().getId())) {
                throw new RegleMetierException("AUTO_RELECTURE", HttpStatus.FORBIDDEN,
                        "Vous ne pouvez pas relire votre propre exercice.");
            }
            if (!etudiantDeclencheur.equals(relecture.getRelecteur().getId())) {
                throw new RegleMetierException("RELECTEUR_NON_AFFECTE", HttpStatus.FORBIDDEN,
                        "Seul le relecteur affecté peut rendre cette relecture.");
            }
        }

        // RG14 (contradiction C1 tranchee contre Q10) : une relecture rendue est definitive.
        if (relecture.getRendueAt() != null) {
            throw new RegleMetierException("RELECTURE_DEJA_RENDUE", HttpStatus.CONFLICT,
                    "Cette relecture a déjà été rendue : elle est définitive.");
        }

        // RG15 : aucune relecture apres la cloture de la session.
        if (relecture.getExercice().getSession().getClotureeAt() != null) {
            throw new RegleMetierException("SESSION_CLOTUREE", HttpStatus.CONFLICT,
                    "La session est clôturée : les relectures sont fermées.");
        }

        relecture.setNote(note.shortValue());
        relecture.setCommentaire(commentaire);
        relecture.setRendueAt(OffsetDateTime.now(clock));
        Relecture rendue = relectures.save(relecture);

        // Issue #34 : l'exercice passe en RELU quand les deux relecteurs ont
        // rendu ; tant qu'un seul a rendu, sa note reste provisoire.
        Exercice exercice = relecture.getExercice();
        long rendues = relectures.findByExerciceId(exercice.getId()).stream()
                .filter(r -> r.getRendueAt() != null)
                .count();
        if (rendues >= 2) {
            exercice.setStatut(Exercice.Statut.RELU);
            exercices.save(exercice);
        }

        return rendue;
    }
}
