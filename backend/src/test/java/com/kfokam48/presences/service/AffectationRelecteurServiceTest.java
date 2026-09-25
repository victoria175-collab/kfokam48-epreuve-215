package com.kfokam48.presences.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * B6 : test unitaire sur une règle métier réelle — le tirage du relecteur
 * (EF4, RG11, RG12, décision Z2).
 */
class AffectationRelecteurServiceTest {

    private final AffectationRelecteurService service = new AffectationRelecteurService();

    @Test
    void aucunCandidatRenvoieNull() {
        // Z2 : auteur seul présent -> pas de relecteur, l'exercice reste DEPOSE.
        assertThat(service.choisirRelecteur(1L, List.of(new AffectationRelecteurService.Candidat(1L, 0))))
                .isNull();
        assertThat(service.choisirRelecteur(1L, List.of())).isNull();
    }

    @Test
    void lauteurNestJamaisChoisi() {
        // RG12 : parmi les présents, l'auteur est exclu même s'il est le moins chargé.
        for (int i = 0; i < 20; i++) {
            Long choisi = service.choisirRelecteur(1L, List.of(
                    new AffectationRelecteurService.Candidat(1L, 0),
                    new AffectationRelecteurService.Candidat(2L, 5)));
            assertThat(choisi).isEqualTo(2L);
        }
    }

    @Test
    void leMoinsChargeEstToujoursChoisi() {
        // RG11 : le tirage se fait parmi les présents les moins chargés.
        for (int i = 0; i < 20; i++) {
            Long choisi = service.choisirRelecteur(1L, List.of(
                    new AffectationRelecteurService.Candidat(2L, 0),
                    new AffectationRelecteurService.Candidat(3L, 5),
                    new AffectationRelecteurService.Candidat(4L, 5)));
            assertThat(choisi).isEqualTo(2L);
        }
    }

    @Test
    void tirageParmiLesExAequoMoinsCharges() {
        // RG11 : à charge égale, le hasard décide, mais uniquement parmi les ex aequo.
        for (int i = 0; i < 50; i++) {
            Long choisi = service.choisirRelecteur(1L, List.of(
                    new AffectationRelecteurService.Candidat(2L, 3),
                    new AffectationRelecteurService.Candidat(3L, 3),
                    new AffectationRelecteurService.Candidat(4L, 9)));
            assertThat(choisi).isIn(2L, 3L);
        }
    }
}
