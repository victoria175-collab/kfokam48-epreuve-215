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
 * Issue #9 (ENF4, B2, B4) : toute erreur répond au format imposé { code, message },
 * sans stack trace. Le cas « corps JSON mal formé » est couvert dans le test
 * d'intégration de POST /api/sessions (issue #1), première route de l'API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FormatErreurIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uneRouteInexistanteRepondAuFormatImposé() throws Exception {
        mockMvc.perform(get("/api/cette-route-nexiste-pas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ROUTE_INCONNUE"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
