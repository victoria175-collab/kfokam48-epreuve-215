package com.kfokam48.presences.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Issue #4 (EF4, RG10, RG11, RG12, Z2) : scenario complet du depot a
 * l'affectation, avec reprise des exercices restes en attente de relecteur.
 * Jeu d'essai V2 : presents = Amina (1), Bertrand (2), Chantal (3) ;
 * l'exercice d'Amina est deja affecte a Bertrand (relecture ouverte).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AffectationIntegrationTest {

    private static final String CODE_DEMO = "KD2M4A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExerciceRepository exercices;

    @Autowired
    private RelectureRepository relectures;

    @Autowired
    private DataSource dataSource;

    @Test
    void depotSansRelecteurDisponibleResteDeposePuisSenAttenteApresUneNouvellePresence() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        // Scenario Z2 isole : seule Chantal (3) reste presente, sans l'exercice
        // de demonstration elle n'a aucun autre present -> aucun candidat.
        jdbc.update("DELETE FROM presence WHERE session_id = 1 AND etudiant_id IN (1, 2)");
        jdbc.update("DELETE FROM relecture WHERE exercice_id = 1");
        jdbc.update("UPDATE exercice SET statut = 'DEPOSE' WHERE id = 1");

        MvcResult resultat = mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":3,"
                                + "\"lien\":\"https://github.com/chantal/exo\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = new ObjectMapper().readTree(resultat.getResponse().getContentAsString());
        assertEquals("DEPOSE", body.get("statut").asText(),
                "Aucun autre present : l'exercice doit rester DEPOSE (Z2)");

        // Desire (4) marque sa presence : la reprise des affectations (Z2) doit
        // desormais trouver un candidat et affecter l'exercice de Chantal.
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":4}"))
                .andExpect(status().isCreated());

        Long idExercice = body.get("id").asLong();
        Exercice exo = exercices.findById(idExercice).orElseThrow();
        assertEquals(Exercice.Statut.EN_ATTENTE_RELECTURE, exo.getStatut(),
                "La presence de Desire doit deverrouiller l'affectation (Z2)");

        Relecture relecture = relectures.findByExerciceSessionId(1L).stream()
                .filter(r -> r.getExercice().getId().equals(idExercice))
                .findFirst()
                .orElseThrow();
        assertNotNull(relecture.getRelecteur());
        assertNotEquals(exo.getAuteur().getId(), relecture.getRelecteur().getId(),
                "RG12 : le relecteur n'est jamais l'auteur");
    }

    @Test
    void depotAvecUnAutrePresentPasseDirectementEnAttenteRelecture() throws Exception {
        // RG10, RG11, RG12 : Amina (1), Bertrand (2) et Chantal (3) sont presents
        // dans V2 ; le depot de Desire (4) est affecte immediatement.
        MvcResult resultat = mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":4,"
                                + "\"lien\":\"https://github.com/desire/exo\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = new ObjectMapper().readTree(resultat.getResponse().getContentAsString());
        assertEquals("EN_ATTENTE_RELECTURE", body.get("statut").asText());

        Relecture relecture = relectures.findByExerciceSessionId(1L).stream()
                .filter(r -> r.getExercice().getId().equals(body.get("id").asLong()))
                .findFirst()
                .orElseThrow();
        assertNotNull(relecture.getRelecteur());
    }
}
