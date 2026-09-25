package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * EF3 : l'étudiant dépose le lien de son exercice. Règles : RG6 (un exercice
 * par session et par auteur), RG7 (URL absolue http(s) de 2048 caractères au
 * plus), RG8 (dépôt possible après expiration du code, jusqu'à la clôture),
 * RG21 (étudiant hors promotion refusé). L'affectation d'un relecteur (EF4)
 * sera branchée ici par l'issue #4.
 */
@Service
public class ExerciceService {

    private final SessionCoursRepository sessions;
    private final EtudiantRepository etudiants;
    private final ExerciceRepository exercices;
    private final AffectationRelecteurService affectation;
    private final Clock clock;

    public ExerciceService(SessionCoursRepository sessions,
                           EtudiantRepository etudiants,
                           ExerciceRepository exercices,
                           AffectationRelecteurService affectation,
                           Clock clock) {
        this.sessions = sessions;
        this.etudiants = etudiants;
        this.exercices = exercices;
        this.affectation = affectation;
        this.clock = clock;
    }

    /**
     * Issue #33 (même logique que la présence) : pas de transaction englobante.
     * L'exercice est enregistré immédiatement, puis l'affectation s'exécute
     * dans sa propre transaction ; son échec (conflit simultané) ne doit pas
     * annuler le dépôt, seulement laisser l'exercice DEPOSE (Z2).
     */
    public Exercice deposer(long sessionId, long etudiantId, String lien) {
        SessionCours session = sessions.findById(sessionId)
                .orElseThrow(() -> new RegleMetierException("SESSION_INCONNUE", HttpStatus.NOT_FOUND,
                        "La session demandée n'existe pas."));

        Etudiant etudiant = etudiants.findById(etudiantId)
                .orElseThrow(() -> new RegleMetierException("ETUDIANT_INCONNU", HttpStatus.BAD_REQUEST,
                        "L'étudiant indiqué n'existe pas."));

        // RG21 : un étudiant n'agit que sur les sessions de sa propre promotion.
        if (!session.getPromotion().getId().equals(etudiant.getPromotion().getId())) {
            throw new RegleMetierException("ETUDIANT_HORS_PROMOTION", HttpStatus.FORBIDDEN,
                    "Cette session n'appartient pas à votre promotion.");
        }

        // RG8 : le dépôt est possible jusqu'à la clôture, même après expiration du code.
        if (session.getClotureeAt() != null) {
            throw new RegleMetierException("SESSION_CLOTUREE", HttpStatus.CONFLICT,
                    "La session est clôturée : le dépôt est fermé.");
        }

        // RG7 : URL absolue en http ou https.
        validerLien(lien);

        // RG6 : un seul exercice par session et par auteur.
        if (exercices.existsBySessionIdAndAuteurId(session.getId(), etudiant.getId())) {
            throw new RegleMetierException("EXERCICE_DEJA_DEPOSE", HttpStatus.CONFLICT,
                    "Vous avez déjà déposé un exercice pour cette session.");
        }

        Exercice exercice = new Exercice();
        exercice.setSession(session);
        exercice.setAuteur(etudiant);
        exercice.setLien(lien);
        exercice.setStatut(Exercice.Statut.DEPOSE);
        exercice.setDeposeAt(OffsetDateTime.now(clock));
        exercice = exercices.save(exercice);

        // EF4 : si un relecteur est disponible, l'exercice passe tout de suite
        // en EN_ATTENTE_RELECTURE ; sinon il reste DEPOSE (Z2).
        try {
            affectation.affecter(exercice);
        } catch (RuntimeException e) {
            // Conflit d'affectation simultanée : le dépôt survit (issue #33),
            // l'exercice sera repris à la prochaine présence.
        }
        return exercice;
    }

    static void validerLien(String lien) {
        if (lien == null || lien.isBlank()) {
            throw new RegleMetierException("LIEN_INVALIDE", HttpStatus.BAD_REQUEST,
                    "Le lien de l'exercice est obligatoire.");
        }
        if (lien.length() > 2048) {
            throw new RegleMetierException("LIEN_INVALIDE", HttpStatus.BAD_REQUEST,
                    "Le lien dépasse 2048 caractères.");
        }
        try {
            URI uri = new URI(lien);
            String scheme = uri.getScheme();
            if (scheme == null || uri.getHost() == null
                    || !(scheme.equals("http") || scheme.equals("https"))) {
                throw new RegleMetierException("LIEN_INVALIDE", HttpStatus.BAD_REQUEST,
                        "Le lien doit être une URL absolue commençant par http ou https.");
            }
        } catch (URISyntaxException e) {
            throw new RegleMetierException("LIEN_INVALIDE", HttpStatus.BAD_REQUEST,
                    "Le lien n'est pas une URL valide.");
        }
    }
}
