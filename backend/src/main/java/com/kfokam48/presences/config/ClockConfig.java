package com.kfokam48.presences.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Horloge injectee dans les services : permet de figer le temps dans les tests
 * pour couvrir l'expiration du code (RG1) et le blocage apres cinq echecs (RG4)
 * sans aucune attente reelle.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
