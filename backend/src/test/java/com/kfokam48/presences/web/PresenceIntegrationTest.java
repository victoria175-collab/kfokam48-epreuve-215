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
 * Issue #2 (EF2, RG1, RG2, RG3, RG21) : POST /api/presences. Le code de la
 * session de demonstration (V2) sert de jeu d'essai ; l'expiration est couverte
 * en avancant expiration_at en base, ce qui exerce exactement le test RG1.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PresenceIntegrationTest {

    private static final String CODE_DEMO = "KD2M4A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Test
    void marquageNominalRepond201AvecSourceEtudiant() throws Exception {
        // Les etudiants 1, 2 et 3 sont deja presents (V2) : le 4 marque sa presence.
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":4}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sessionId").value(1))
                .andExpect(jsonPath("$.etudiantId").value(4))
                .andExpect(jsonPath("$.source").value("ETUDIANT"));
    }

    @Test
    void codeInconnuRepond400() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"ZZZZZZ\",\"etudiantId\":4}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"));
    }

    @Test
    void codeDuneAutrePromotionRepond400CommeInconnu() throws Exception {
        // RG21 : un etudiant d'une autre promotion est cree pour l'occasion.
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("INSERT INTO promotion (id, nom) VALUES (2, 'Autre promotion')");
        jdbc.update("INSERT INTO etudiant (id, nom, promotion_id) VALUES (99, 'Hors Promotion', 2)");

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":99}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"));
    }

    @Test
    void secondMarquageRepond409DejaPresent() throws Exception {
        // RG3 : l'etudiant 1 a deja une presence dans la session de demonstration.
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"));
    }

    @Test
    void codeExpireRepond410() throws Exception {
        // RG1 : expiration_at passe dans le passe, le code est refuse.
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("UPDATE session_cours SET expiration_at = CURRENT_TIMESTAMP - INTERVAL '1' MINUTE WHERE id = 1");

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":5}"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("CODE_EXPIRE"));
    }

    @Test
    void corpsSansEtudiantRepond400AuFormatImposé() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").isString())
                .andExpect(jsonPath("$.message").isString());
    }
}
