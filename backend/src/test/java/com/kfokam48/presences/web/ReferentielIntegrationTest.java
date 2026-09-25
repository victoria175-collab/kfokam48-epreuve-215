package com.kfokam48.presences.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Issue #8 (EF8) : les listes promotions, etudiants et sessions alimentent les
 * ecrans ; aucune ne contient le code de presence (Z3). Les donnees de
 * demonstration (V2) servent de jeu d'essai.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReferentielIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listeDesPromotions() throws Exception {
        mockMvc.perform(get("/api/promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").isString());
    }

    @Test
    void listeDesEtudiantsDunePromotion() throws Exception {
        mockMvc.perform(get("/api/promotions/1/etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].nom").isString());
    }

    @Test
    void etudiantsDunePromotionInconnueRepondent404() throws Exception {
        mockMvc.perform(get("/api/promotions/9999/etudiants"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }

    @Test
    void listeDesSessionsSansLeCodeDePresence() throws Exception {
        mockMvc.perform(get("/api/sessions?promotionId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").isString())
                .andExpect(jsonPath("$[0].code").doesNotExist());
    }

    @Test
    void sessionsDunePromotionInconnueRepondent404() throws Exception {
        mockMvc.perform(get("/api/sessions?promotionId=9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }
}
