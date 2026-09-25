package com.kfokam48.presences.service;

import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

/**
 * EF4, RG11 : le relecteur est choisi au hasard parmi les étudiants présents à la
 * session de l'exercice, auteur exclu, en favorisant les moins chargés dans la session.
 * Z2 : si aucun candidat n'existe, l'affectation n'est pas faite (retentée à chaque
 * nouvelle présence). La règle de tirage est isolée du dépôt pour rester testable
 * en pur unitaire (issue #4).
 */
@Service
public class AffectationRelecteurService {

    private final Random random = new Random();

    /** Un candidat au tirage : un étudiant présent, avec sa charge de relectures dans la session. */
    public record Candidat(long etudiantId, long charge) {
    }

    /**
     * Renvoie l'identifiant du relecteur choisi, ou null si aucun candidat n'est
     * disponible (Z2 : l'exercice reste DEPOSE).
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
}
