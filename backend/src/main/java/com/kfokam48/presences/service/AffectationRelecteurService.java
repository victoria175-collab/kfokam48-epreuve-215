package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.Presence;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * EF4 : affectation automatique d'un relecteur. RG10 : exactement une relecture
 * par exercice. RG11 : tirage au hasard parmi les étudiants présents à la
 * session, en favorisant ceux qui ont le moins de relectures affectées dans la
 * session. RG12 : l'auteur ne relit jamais son propre exercice. Z2 : sans
 * candidat, la relecture n'est pas créée (l'exercice reste DEPOSE) et
 * l'affectation est retentée à chaque nouvelle présence dans la session.
 */
@Service
public class AffectationRelecteurService {

    private final PresenceRepository presences;
    private final RelectureRepository relectures;
    private final ExerciceRepository exercices;
    private final EtudiantRepository etudiants;
    private final Clock clock;
    private final Random random = new Random();

    public AffectationRelecteurService(PresenceRepository presences,
                                       RelectureRepository relectures,
                                       ExerciceRepository exercices,
                                       EtudiantRepository etudiants,
                                       Clock clock) {
        this.presences = presences;
        this.relectures = relectures;
        this.exercices = exercices;
        this.etudiants = etudiants;
        this.clock = clock;
    }

    /** Un candidat au tirage : un étudiant présent, avec sa charge de relectures dans la session. */
    public record Candidat(long etudiantId, long charge) {
    }

    /**
     * Version transactionnelle indépendante, appelée par la reprise (issue #33)
     * : tourne dans sa propre transaction quelle que soit la transaction
     * appelante, pour qu'un échec d'affectation n'annule jamais une présence.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean affecterDansSaPropreTransaction(Long exerciceId) {
        Exercice exercice = exercices.findById(exerciceId)
                .orElseThrow(() -> new IllegalStateException("Exercice introuvable : " + exerciceId));
        return affecter(exercice);
    }

    /**
     * Tente d'affecter les relecteurs de l'exercice (issue #34 : deux pairs
     * différents). Renvoie true si l'exercice porte au moins une relecture et
     * passe alors en EN_ATTENTE_RELECTURE ; false s'il reste sans candidat
     * (Z2, l'exercice reste DEPOSE). Rangs posés séparément : chaque rang est
     * complété quand un candidat devient disponible (présence suivante).
     */
    @Transactional
    public boolean affecter(Exercice exercice) {
        long dejaAffectees = relectures.countByExerciceId(exercice.getId());
        if (dejaAffectees >= 2) {
            return true; // les deux relecteurs sont en place
        }

        boolean auMoinsUne = dejaAffectees > 0;
        Set<Long> dejaChoisis = new HashSet<>(relectures.findByExerciceId(exercice.getId()).stream()
                .map(r -> r.getRelecteur().getId())
                .toList());

        for (short rang = 1; rang <= 2; rang++) {
            if (rang <= dejaAffectees) {
                continue;
            }
            List<Candidat> candidats = candidatsDeLaSession(exercice.getSession().getId()).stream()
                    .filter(c -> !dejaChoisis.contains(c.etudiantId()))
                    .toList();
            Long relecteurId = choisirRelecteur(exercice.getAuteur().getId(), candidats);
            if (relecteurId == null) {
                break; // Z2 : pas de second candidat disponible pour l'instant
            }
            Etudiant relecteur = etudiants.findById(relecteurId).orElseThrow();
            Relecture relecture = new Relecture();
            relecture.setExercice(exercice);
            relecture.setRelecteur(relecteur);
            relecture.setRang(rang);
            relecture.setAffecteeAt(OffsetDateTime.now(clock));
            relectures.save(relecture);
            dejaChoisis.add(relecteurId);
            auMoinsUne = true;
        }

        if (auMoinsUne) {
            exercice.setStatut(Exercice.Statut.EN_ATTENTE_RELECTURE);
            exercices.save(exercice);
        }
        return auMoinsUne;
    }

    /**
     * Retente l'affectation de tous les exercices encore DEPOSE de la session
     * (Z2) : appelé à chaque nouvelle présence enregistrée dans la session.
     */
    @Transactional
    public void retesterAffectations(Long sessionId) {
        exercices.findBySessionIdAndStatut(sessionId, Exercice.Statut.DEPOSE)
                .forEach(this::affecter);
    }

    /**
     * Règle de tirage pure (RG11, RG12), testée en pur unitaire : renvoie
     * l'identifiant du relecteur choisi, ou null si aucun candidat.
     */
    public Long choisirRelecteur(long auteurId, List<Candidat> presents) {
        List<Candidat> candidats = presents.stream()
                .filter(c -> c.etudiantId() != auteurId) // RG12 : jamais l'auteur
                .toList();

        if (candidats.isEmpty()) {
            return null;
        }

        long chargeMin = candidats.stream().mapToLong(Candidat::charge).min().orElse(0L);
        List<Long> moinsCharges = candidats.stream()
                .filter(c -> c.charge() == chargeMin)
                .map(Candidat::etudiantId)
                .toList();

        // Tirage au hasard parmi les moins chargés (RG11, répartition de la charge).
        return moinsCharges.get(random.nextInt(moinsCharges.size()));
    }

    /** Candidats : les présents de la session, avec leur charge de relectures affectées (RG11). */
    private List<Candidat> candidatsDeLaSession(Long sessionId) {
        Map<Long, Long> chargeParEtudiant = new HashMap<>();
        relectures.findByExerciceSessionId(sessionId).stream()
                .filter(r -> r.getRendueAt() == null)
                .forEach(r -> chargeParEtudiant.merge(r.getRelecteur().getId(), 1L, Long::sum));
        return presences.findBySessionId(sessionId).stream()
                .map(Presence::getEtudiant)
                .map(e -> new Candidat(e.getId(), chargeParEtudiant.getOrDefault(e.getId(), 0L)))
                .toList();
    }
}
