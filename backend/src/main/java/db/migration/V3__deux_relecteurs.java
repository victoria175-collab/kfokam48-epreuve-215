package db.migration;

import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * V3 - Deux relecteurs par exercice (issue #34, enveloppe étape 3).
 *
 * Changement de besoin : chaque exercice est relu par deux pairs différents ;
 * la note retenue est la moyenne des deux ; si un seul a rendu, sa note est
 * affichée en attendant, marquée comme provisoire.
 *
 * Migration additive (règle 0.11) : V1 et V2 ne sont pas touchées, les données
 * de démonstration survivent telles quelles. La contrainte d'unicité stricte
 * posée en V1 (sans nom explicite, donc différent entre H2 et PostgreSQL) est
 * remplacée par une limite de deux relectures par exercice, de rangs distincts
 * (RG10 remplacée) : le nom de l'index unique est découvert via les métadonnées
 * JDBC, en regroupant les colonnes de chaque index.
 */
public class V3__deux_relecteurs extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(
                new SingleConnectionDataSource(context.getConnection(), true));

        String nomContrainte = trouverContrainteUniqueSurExerciceSeul(jdbc);

        // L'ancienne unicité stricte cède la place à la limite de deux.
        if (nomContrainte != null) {
            jdbc.update("ALTER TABLE relecture DROP CONSTRAINT \"" + nomContrainte + "\"");
        }
        jdbc.execute("ALTER TABLE relecture ADD COLUMN rang SMALLINT NOT NULL DEFAULT 1");
        jdbc.execute("ALTER TABLE relecture "
                + "ADD CONSTRAINT uq_relecture_exercice_rang UNIQUE (exercice_id, rang)");
        jdbc.execute("ALTER TABLE relecture "
                + "ADD CONSTRAINT ck_relecture_rang CHECK (rang IN (1, 2))");
    }

    /**
     * Trouve la contrainte UNIQUE de relecture portant exactement sur la
     * colonne exercice_id seule (posée en V1 sans nom explicite, donc généré :
     * différent entre H2 et PostgreSQL). La requête information_schema est
     * portable et insensible à la casse.
     */
    private String trouverContrainteUniqueSurExerciceSeul(JdbcTemplate jdbc) {
        List<String> noms = new ArrayList<>();
        String sql = "SELECT tc.constraint_name "
                + "FROM information_schema.table_constraints tc "
                + "JOIN information_schema.key_column_usage k "
                + "  ON k.constraint_name = tc.constraint_name "
                + " AND LOWER(k.table_name) = 'relecture' "
                + "WHERE LOWER(tc.table_name) = 'relecture' "
                + "  AND tc.constraint_type = 'UNIQUE' "
                + "GROUP BY tc.constraint_name "
                + "HAVING COUNT(*) = 1 AND MIN(LOWER(k.column_name)) = 'exercice_id'";
        noms.addAll(jdbc.queryForList(sql, String.class));
        return noms.isEmpty() ? null : noms.get(0);
    }
}
