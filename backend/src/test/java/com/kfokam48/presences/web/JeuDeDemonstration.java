package com.kfokam48.presences.web;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Restaure le jeu de demonstration (V2) pour les tests qui ne sont pas
 * transactionnels : les presences et affectations s'executent desormais hors
 * de la transaction de test (issue #33), elles mutent donc la base partagee.
 * Les sequences sont repositionnees au-dela de 100 pour qu'aucun identifiant
 * genere ne collide jamais avec les identifiants explicites du jeu de demo.
 */
public final class JeuDeDemonstration {

    private JeuDeDemonstration() {
    }

    public static void restaurer(DataSource dataSource) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.update("DELETE FROM relecture");
        jdbc.update("DELETE FROM exercice");
        jdbc.update("DELETE FROM presence");
        jdbc.update("DELETE FROM session_cours WHERE id <> 1");
        jdbc.update("ALTER TABLE relecture ALTER COLUMN id RESTART WITH 101");
        jdbc.update("ALTER TABLE exercice ALTER COLUMN id RESTART WITH 101");
        jdbc.update("ALTER TABLE presence ALTER COLUMN id RESTART WITH 101");
        jdbc.update("ALTER TABLE session_cours ALTER COLUMN id RESTART WITH 101");

        jdbc.update("INSERT INTO presence (id, session_id, etudiant_id, source, marquee_at) VALUES "
                + "(1, 1, 1, 'ETUDIANT', CURRENT_TIMESTAMP), "
                + "(2, 1, 2, 'ETUDIANT', CURRENT_TIMESTAMP), "
                + "(3, 1, 3, 'ETUDIANT', CURRENT_TIMESTAMP)");
        jdbc.update("INSERT INTO exercice (id, session_id, etudiant_id, lien, statut, depose_at) "
                + "VALUES (1, 1, 1, 'https://github.com/amina-ngb/exercice-1', "
                + "'EN_ATTENTE_RELECTURE', CURRENT_TIMESTAMP)");
        jdbc.update("INSERT INTO relecture (id, exercice_id, relecteur_id, affectee_at, rendue_at) "
                + "VALUES (1, 1, 2, CURRENT_TIMESTAMP, NULL)");
    }
}
