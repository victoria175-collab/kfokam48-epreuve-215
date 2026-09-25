package com.kfokam48.presences.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issue #7 (EF7, RG17, RG18, C2) : GET /api/tableau sur le jeu de demonstration.
 * V2 : Amina (1) est présente, a déposé 1 exercice, relu par Bertrand (affectée,
 * non rendue) ; Bertrand (2) a 1 relecture en attente ; les autres, zéro.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TableauIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tableauAvecLesCompteursDuJeuDeDemonstration() throws Exception {
        mockMvc.perform(get("/api/tableau?promotionId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].etudiantId").value(1))
                .andExpect(jsonPath("$[0].presences").value(1))
                .andExpect(jsonPath("$[0].exercicesDeposes").value(1))
                .andExpect(jsonPath("$[0].moyenne").doesNotExist())
                .andExpect(jsonPath("$[1].etudiantId").value(2))
                .andExpect(jsonPath("$[1].relecturesEnAttente").value(1))
                .andExpect(jsonPath("$[3].etudiantId").value(4))
                .andExpect(jsonPath("$[3].presences").value(0));
    }

    @Test
    void moyenneCalculeeParLApiApresRenduDuneRelecture() throws Exception {
        // EF6 : Bertrand rend la relecture de l'exercice d'Amina (note 15).
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"Bien.\"}"))
                .andExpect(status().isOk());

        // RG17 : la moyenne d'Amina vaut 15.00, calculee par l'API (F3).
        mockMvc.perform(get("/api/tableau?promotionId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].moyenne").value(15.0))
                .andExpect(jsonPath("$[1].relecturesEnAttente").value(0));
    }

    @Test
    void promotionInconnueRepond404() throws Exception {
        mockMvc.perform(get("/api/tableau?promotionId=9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }
}
