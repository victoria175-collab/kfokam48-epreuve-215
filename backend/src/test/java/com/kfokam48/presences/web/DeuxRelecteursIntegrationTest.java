package com.kfokam48.presences.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;

/**
 * Issue #34 (enveloppe étape 3) : chaque exercice est relu par deux pairs
 * différents ; la note retenue est la moyenne des deux ; si un seul a rendu,
 * sa note est affichée marquée comme provisoire.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DeuxRelecteursIntegrationTest {

    private static final String CODE_DEMO = "KD2M4A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExerciceRepository exercices;

    @Autowired
    private RelectureRepository relectures;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void restaurerLeJeuDeDemonstration() {
        JeuDeDemonstration.restaurer(dataSource);
    }

    @Test
    void lesDonneesDeDemonstrationOntSurvecuALaMigrationV3() {
        // V2 a une relecture (rang 1) : la migration V3 l'a conservée telle quelle.
        var demo = relectures.findByExerciceId(1L);
        assertEquals(1, demo.size());
        assertEquals((short) 1, demo.get(0).getRang());
        assertEquals(2L, demo.get(0).getRelecteur().getId());
    }

    @Test
    void unDepotRecoitDeuxRelecteursDistinctsQuanAssezDePresents() throws Exception {
        // D'abord deux présences supplémentaires (4 et 5) : Chantal(3) dépose,
        // il faut deux relecteurs distincts autres que l'auteur.
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":4}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":5}"))
                .andExpect(status().isCreated());

        MvcResult depot = mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":1,\"etudiantId\":3,"
                                + "\"lien\":\"https://github.com/chantal/exo\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = new ObjectMapper().readTree(depot.getResponse().getContentAsString());
        Long exerciceId = body.get("id").asLong();

        var affectees = relectures.findByExerciceId(exerciceId);
        assertEquals(2, affectees.size(), "Deux relecteurs doivent être affectés immédiatement");
        assertNotEquals(affectees.get(0).getRelecteur().getId(),
                affectees.get(1).getRelecteur().getId(),
                "Les deux relecteurs doivent être différents");
        assertEquals((short) 1, affectees.get(0).getRang());
        assertEquals((short) 2, affectees.get(1).getRang());
    }

    @Test
    void noteProvisoirePuisMoyenneDefinitiveCoteEtudiant() throws Exception {
        // L'exercice de démo (d'Amina) a une relecture ouverte (Bertrand, rang 1).
        // On affecte le rang 2 avec la présence de Desire (4), qui relira en second.
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":4}"))
                .andExpect(status().isCreated());
        // La reprise a affecté le rang 2 à un étudiant présent distinct de Bertrand.
        var affectees = relectures.findByExerciceId(1L);
        assertEquals(2, affectees.size());
        Long relecteur2 = affectees.stream().filter(r -> r.getRang() == 2).findFirst().orElseThrow()
                .getRelecteur().getId();
        Long idRelecture2 = affectees.stream().filter(r -> r.getRang() == 2).findFirst().orElseThrow()
                .getId();

        // Bertrand (rang 1) rend : la note de l'exercice devient provisoire.
        mockMvc.perform(post("/api/relectures/1")
                        .header("X-Etudiant-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":10,\"commentaire\":\"Correct.\"}"))
                .andExpect(status().isOk());

        // L'exercice n'est PAS encore RELU : un seul rendu sur deux (issue #34).
        Exercice exo = exercices.findById(1L).orElseThrow();
        assertEquals(Exercice.Statut.EN_ATTENTE_RELECTURE, exo.getStatut(),
                "Un seul rendu : l'exercice reste en attente de la seconde relecture");

        mockMvc.perform(get("/api/etudiants/1/exercices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].note").value(10.0))
                .andExpect(jsonPath("$[0].noteProvisoire").value(true));

        // Le second relecteur rend 16 : la note retenue devient la moyenne (13.00).
        mockMvc.perform(post("/api/relectures/" + idRelecture2)
                        .header("X-Etudiant-Id", relecteur2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":16,\"commentaire\":\"Très bien.\"}"))
                .andExpect(status().isOk());

        assertEquals(Exercice.Statut.RELU, exercices.findById(1L).orElseThrow().getStatut());
        mockMvc.perform(get("/api/etudiants/1/exercices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].note").value(13.0))
                .andExpect(jsonPath("$[0].noteProvisoire").value(false));
    }

    @Test
    void etudiantInconnuRepond404() throws Exception {
        mockMvc.perform(get("/api/etudiants/9999/exercices"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }
}
