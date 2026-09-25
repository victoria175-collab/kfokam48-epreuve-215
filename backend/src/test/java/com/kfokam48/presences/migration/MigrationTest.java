package com.kfokam48.presences.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * ENF6, ENF7, B5 : une base vide doit atteindre le schema complet par les seules
 * migrations Flyway, avec les donnees de demonstration chargees. Le contexte de
 * test demarre sur H2 en memoire vierge (mode PostgreSQL, voir
 * src/test/resources/application.yml) : tout ce que voit ce test vient donc des
 * seules migrations. Le test repasse explicitement Flyway pour verifier aussi
 * l'idempotence, et il passe sur un poste vierge, sans base locale ni Docker.
 */
@SpringBootTest
class MigrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void uneBaseVideAtteintLeSchemaCompletParLesSeulesMigrations() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        // Flyway a deja migre la base vide au demarrage du contexte. Un nouveau
        // migrate ne doit rien faire de plus : le schema provient bien des seules
        // migrations V1 (schema) et V2 (donnees de demonstration).
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
        MigrateResult resultat = flyway.migrate();
        assertThat(resultat.migrationsExecuted).isZero();

        // Toutes les tables de D2 existent.
        List<String> tables = jdbc.queryForList(
                "SELECT LOWER(table_name) FROM information_schema.tables WHERE table_type = 'BASE TABLE'",
                String.class);
        assertThat(tables).contains("promotion", "etudiant", "session_cours",
                "presence", "exercice", "relecture", "tentative_code", "flyway_schema_history");

        // V2 a charge les donnees de demonstration (ENF6, decision Z4).
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM promotion", Integer.class)).isGreaterThanOrEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM etudiant", Integer.class)).isGreaterThanOrEqualTo(7);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM session_cours", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM presence", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM exercice", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM relecture", Integer.class)).isEqualTo(1);

        // La session de demonstration est ouverte et porte un code a 6 caracteres (RG22).
        assertThat(jdbc.queryForObject("SELECT LENGTH(code) FROM session_cours WHERE id = 1", Integer.class))
                .isEqualTo(6);
        assertThat(jdbc.queryForObject(
                "SELECT COUNT(*) FROM session_cours WHERE id = 1 AND cloturee_at IS NULL", Integer.class))
                .isEqualTo(1);
    }
}
