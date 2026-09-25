package com.kfokam48.presences.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.repository.ExerciceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues #5 et #6 (EF5, EF6, RG12, RG13, RG14, RG16, Z11) : jeu d'essai V2 —
 * l'exercice d'Amina (1) est affecte a Bertrand (2), relecture ouverte.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RelectureIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExerciceRepository exercices;

    @Test
    void consultationDesRelecturesDuRelecteur() throws Exception {
        mockMvc.perform(get("/api/relectures?relecteurId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].exerciceId").value(1))
                .andExpect(jsonPath("$[0].lienExercice").isString())
                .andExpect(jsonPath("$[0].sessionTitre").isString())
                .andExpect(jsonPath("$[0].rendue").value(false))
                .andExpect(jsonPath("$[0].auteur").doesNotExist())
                .andExpect(jsonPath("$[0].relecteurId").doesNotExist());
    }

    @Test
    void consultationDunEtudiantInconnuRepond400() throws Exception {
        mockMvc.perform(get("/api/relectures?relecteurId=9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }

    @Test
    void renduNominalRepond200EtLExercicePasseEnReLu() throws Exception {
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"Travail solide.\"}"))
                .andExpect(status().isOk());

        // RG20 / D4 : l'exercice relu est definitivement en RELU.
        Exercice exercice = exercices.findById(1L).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(Exercice.Statut.RELU, exercice.getStatut());
    }

    @Test
    void noteHorsBornesRepond400NoteInvalide() throws Exception {
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":25,\"commentaire\":\"...\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    void noteDecimaleRepond400NoteInvalide() throws Exception {
        // RG13 : une note decimale n'est jamais tronquee silencieusement (section 8
        // du cahier des charges) ; elle est refusee comme NOTE_INVALIDE.
        mockMvc.perform(post("/api/relectures/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":12.5,\"commentaire\":\"...\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    void autoRelectureRepond403() throws Exception {
        // RG12 : Amina (1), auteure de l'exercice, tente de relire son propre travail.
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"...\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTO_RELECTURE"));
    }

    @Test
    void relecteurNonAffecteRepond403() throws Exception {
        // Z11 : Chantal (3) n'est pas le relecteur affecte.
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"...\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("RELECTEUR_NON_AFFECTE"));
    }

    @Test
    void secondRenduRepond409RelectureDejaRendue() throws Exception {
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"Premier rendu.\"}"))
                .andExpect(status().isOk());

        // RG14 (C1 tranchee contre Q10) : une relecture rendue est definitive.
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":18,\"commentaire\":\"Tentative de changement.\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RELECTURE_DEJA_RENDUE"));
    }

    @Test
    void relectureInconnueRepond404() throws Exception {
        mockMvc.perform(post("/api/relectures/9999")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"...\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RELECTURE_INCONNUE"));
    }
}
