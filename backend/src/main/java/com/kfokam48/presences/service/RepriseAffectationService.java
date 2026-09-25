package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.repository.ExerciceRepository;
import java.util.ArrayList;
import java.util.List;
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
     * Issue #34 : la reprise couvre les exercices sans relecteur (DEPOSE) ET
     * ceux qui n'ont qu'un seul des deux relecteurs (EN_ATTENTE_RELECTURE).
     */
    public void reprendre(Long sessionId) {
        List<Exercice> aReprendre = new ArrayList<>();
        aReprendre.addAll(exercices.findBySessionIdAndStatut(sessionId, Exercice.Statut.DEPOSE));
        aReprendre.addAll(exercices.findBySessionIdAndStatut(
                sessionId, Exercice.Statut.EN_ATTENTE_RELECTURE));
        for (Exercice exercice : aReprendre) {
            try {
                affectation.affecterDansSaPropreTransaction(exercice.getId());
            } catch (RuntimeException e) {
                // Conflit d'affectation simultanée : l'exercice reste en l'état,
                // il sera repris à la prochaine présence. La présence est sauve.
                log.info("Affectation retentee plus tard pour l'exercice {} : {}",
                        exercice.getId(), e.getMessage());
            }
        }
    }
}
