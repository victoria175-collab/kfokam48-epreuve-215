package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.repository.ExerciceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Correctif issue #33 : la ré-affectation des exercices en attente (Z2) ne
 * partage plus la transaction de la présence. Chaque affectation s'exécute
 * dans sa propre transaction (REQUIRES_NEW) : si elle échoue — par exemple
 * quand deux présences simultanées tentent d'affecter le même exercice — la
 * présence correspondante reste enregistrée, ce que demandait le client.
 */
@Service
public class RepriseAffectationService {

    private static final Logger log = LoggerFactory.getLogger(RepriseAffectationService.class);

    private final ExerciceRepository exercices;
    private final AffectationRelecteurService affectation;

    public RepriseAffectationService(ExerciceRepository exercices,
                                     AffectationRelecteurService affectation) {
        this.exercices = exercices;
        this.affectation = affectation;
    }

    /**
     * Reprise des affectations de la session, appelée APRÈS le commit de la
     * présence (issue #33). Chaque affectation s'exécute dans sa propre
     * transaction et voit la présence commitée. Aucune exception ne remonte :
     * un échec d'affectation n'est jamais une raison d'annuler une présence.
     */
    public void reprendre(Long sessionId) {
        for (Exercice exercice : exercices.findBySessionIdAndStatut(sessionId, Exercice.Statut.DEPOSE)) {
            try {
                affectation.affecterDansSaPropreTransaction(exercice.getId());
            } catch (RuntimeException e) {
                // Conflit d'affectation simultanée : l'exercice reste DEPOSE,
                // il sera repris à la prochaine présence. La présence est sauve.
                log.info("Affectation retentee plus tard pour l'exercice {} : {}",
                        exercice.getId(), e.getMessage());
            }
        }
    }
}
