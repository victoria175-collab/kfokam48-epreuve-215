package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Presence;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EF2 : l'étudiant marque sa présence avec le code affiché par le formateur.
 * Règles couvertes : RG1 (expiration 15 min), RG2 (clôture refuse le code),
 * RG3 (une présence par session), RG21 (session d'une autre promotion traitée
 * comme inconnue). Un code expiré n'est pas une devinette (RG4) : le compteur
 * d'échecs sera géré par l'issue #16.
 */
@Service
public class PresenceService {

    private final SessionCoursRepository sessions;
    private final EtudiantRepository etudiants;
    private final PresenceRepository presences;
    private final AffectationRelecteurService affectation;
    private final Clock clock;

    public PresenceService(SessionCoursRepository sessions,
                           EtudiantRepository etudiants,
                           PresenceRepository presences,
                           AffectationRelecteurService affectation,
                           Clock clock) {
        this.sessions = sessions;
        this.etudiants = etudiants;
        this.presences = presences;
        this.affectation = affectation;
        this.clock = clock;
    }

    @Transactional
    public Presence marquer(String code, long etudiantId) {
        Etudiant etudiant = etudiants.findById(etudiantId)
                .orElseThrow(() -> new RegleMetierException("ETUDIANT_INCONNU", HttpStatus.BAD_REQUEST,
                        "L'étudiant indiqué n'existe pas."));

        SessionCours session = sessions.findByCode(code)
                .orElseThrow(() -> new RegleMetierException("CODE_INCONNU", HttpStatus.BAD_REQUEST,
                        "Ce code de présence ne correspond à aucune session."));

        // RG21 : le code d'une autre promotion est traité comme inconnu.
        if (!session.getPromotion().getId().equals(etudiant.getPromotion().getId())) {
            throw new RegleMetierException("CODE_INCONNU", HttpStatus.BAD_REQUEST,
                    "Ce code de présence ne correspond à aucune de vos sessions.");
        }

        // RG1 : à partir de expirationAt inclus, le code est refusé.
        OffsetDateTime maintenant = OffsetDateTime.now(clock);
        if (!maintenant.isBefore(session.getExpirationAt())) {
            throw new RegleMetierException("CODE_EXPIRE", HttpStatus.GONE,
                    "Le code de présence a expiré.");
        }

        // RG2 : une session clôturée refuse le code, même avant 15 minutes.
        if (session.getClotureeAt() != null) {
            throw new RegleMetierException("CODE_EXPIRE", HttpStatus.GONE,
                    "Le code de présence a expiré.");
        }

        // RG3 : au plus une présence par session, quelle que soit la source.
        if (presences.existsBySessionIdAndEtudiantId(session.getId(), etudiant.getId())) {
            throw new RegleMetierException("DEJA_PRESENT", HttpStatus.CONFLICT,
                    "Vous avez déjà marqué votre présence à cette session.");
        }

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(Presence.Source.ETUDIANT);
        presence.setMarqueeAt(maintenant);
        presence = presences.save(presence);

        // Z2 : cette nouvelle présence peut débloquer l'affectation d'exercices
        // restés DEPOSE faute de relecteur disponible.
        affectation.retesterAffectations(session.getId());
        return presence;
    }
}
