package com.kfokam48.presences.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Issue #3 (EF3, RG6, RG7, RG8, RG21) : POST /api/exercices. L'étudiant 4
 * (présent mais sans exercice dans V2) sert de jeu d'essai principal.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExerciceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Test
    void depotNominalRepond201AvecStatutDepose() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":4,"
                                + "\"lien\":\"https://github.com/desire/exercice-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.statut").value("DEPOSE"));
    }

    @Test
    void lienNonHttpRepond400LienInvalide() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":4,\"lien\":\"ftp://exemple.org/exo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LIEN_INVALIDE"));
    }

    @Test
    void lienSansUrlAbsolueRepond400LienInvalide() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":4,\"lien\":\"mon-exercice.docx\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LIEN_INVALIDE"));
    }

    @Test
    void secondDepotRepond409ExerciceDejaDepose() throws Exception {
        // RG6 : l'étudiant 1 a déjà déposé l'exercice de démonstration (V2).
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":1,"
                                + "\"lien\":\"https://github.com/amina/exercice-1-bis\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCICE_DEJA_DEPOSE"));
    }

    @Test
    void depotParUnEtudiantHorsPromotionRepond403() throws Exception {
        // RG21 : un étudiant d'une autre promotion tente de déposer dans la session 1.
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("INSERT INTO promotion (id, nom) VALUES (2, 'Autre promotion')");
        jdbc.update("INSERT INTO etudiant (id, nom, promotion_id) VALUES (99, 'Hors Promotion', 2)");

        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":99,"
                                + "\"lien\":\"https://github.com/hors/exercice\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ETUDIANT_HORS_PROMOTION"));
    }

    @Test
    void depotSurUneSessionInconnueRepond404() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":9999,\"etudiantId\":4,"
                                + "\"lien\":\"https://github.com/desire/exercice\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
    }
}
