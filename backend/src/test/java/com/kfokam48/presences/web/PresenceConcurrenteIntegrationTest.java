package com.kfokam48.presences.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
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
 * Issue #33 (enveloppe étape 3) : deux étudiants côte à côte tapent le code
 * presque en même temps, une seule présence apparaît dans la liste du
 * formateur. Ce test reproduit le scénario du client : il doit échouer tant
 * que le bug existe, et passer une fois le correctif posé.
 *
 * Scénario : un exercice est resté DEPOSE (déposé alors que son auteur était
 * seul présent). Deux étudiants marquent leur présence simultanément : les
 * deux présences doivent exister en base. Le test n'est pas transactionnel
 * (les requêtes partent de deux threads) : il restaure les données de
 * démonstration après exécution.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PresenceConcurrenteIntegrationTest {

    private static final String CODE_DEMO = "KD2M4A";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void isolerLeScenario() {
        JeuDeDemonstration.restaurer(dataSource);
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        // Scénario du client : la session n'a plus aucune présence, mais un
        // exercice y est resté DEPOSE (déposé quand son auteur était seul).
        jdbc.update("DELETE FROM relecture");
        jdbc.update("DELETE FROM presence");
        jdbc.update("UPDATE exercice SET statut = 'DEPOSE' WHERE id = 1");
        jdbc.update("ALTER TABLE presence ALTER COLUMN id RESTART WITH 301");
    }

    @AfterEach
    void restaurerLesDonneesDeDemonstration() {
        JeuDeDemonstration.restaurer(dataSource);
    }

    @Test
    void deuxPresencesSimultaneesSontToutesLesDeuxEnregistrees() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CyclicBarrier depart = new CyclicBarrier(2);

        Callable<MvcResult> etudiant4 = () -> {
            depart.await(5, TimeUnit.SECONDS);
            return mockMvc.perform(post("/api/presences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":4}"))
                    .andReturn();
        };
        Callable<MvcResult> etudiant5 = () -> {
            depart.await(5, TimeUnit.SECONDS);
            return mockMvc.perform(post("/api/presences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"code\":\"" + CODE_DEMO + "\",\"etudiantId\":5}"))
                    .andReturn();
        };

        List<Future<MvcResult>> resultats = pool.invokeAll(List.of(etudiant4, etudiant5));
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS), "Les deux appels doivent se terminer");

        // Les deux requêtes doivent réussir : aucune présence ne doit disparaître.
        for (Future<MvcResult> futur : resultats) {
            MvcResult resultat = futur.get();
            assertEquals(201, resultat.getResponse().getStatus(),
                    "Chaque étudiant présent une fois doit obtenir 201 ; reçu : "
                            + resultat.getResponse().getStatus()
                            + " — " + resultat.getResponse().getContentAsString());
        }

        // Les deux présences existent en base.
        Integer compteur = new JdbcTemplate(dataSource).queryForObject(
                "SELECT COUNT(*) FROM presence WHERE session_id = 1 AND etudiant_id IN (4, 5)",
                Integer.class);
        assertEquals(2, compteur, "Les deux présences simultanées doivent être enregistrées");
    }
}
