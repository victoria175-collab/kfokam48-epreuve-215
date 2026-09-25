package com.kfokam48.presences.repository;

import com.kfokam48.presences.web.dto.TableauLigneReponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * EF7 : le tableau d'une promotion est calcule par agregation en base (ENF3,
 * pas de boucle N+1). RG17 : la moyenne est la moyenne arithmetique des notes
 * recues sur les exercices relus, arrondie a deux decimales, NULL sans note.
 * RG18 : les relectures affectees non rendues comptent, y compris apres cloture.
 */
@Repository
public class TableauRepository {

    @PersistenceContext
    private EntityManager em;

    public List<TableauLigneReponse> tableauDeLaPromotion(Long promotionId) {
        // Une seule requete (ENF3, pas de N+1) : presences, exercices deposes,
        // notes RECUES (moyenne via les relectures des exercices de l'etudiant,
        // RG17) et relectures qu'il doit encore rendre (RG18), toutes par
        // jointures gauche distinctes.
        List<Object[]> lignes = em.createNativeQuery("""
                SELECT e.id,
                       e.nom,
                       COUNT(DISTINCT p.id) AS presences,
                       COUNT(DISTINCT x.id) AS exercices_deposes,
                       ROUND(AVG(rr.note), 2) AS moyenne,
                       COUNT(DISTINCT CASE WHEN ra.rendue_at IS NULL THEN ra.id END) AS relectures_en_attente
                FROM etudiant e
                LEFT JOIN presence p ON p.etudiant_id = e.id
                LEFT JOIN exercice x ON x.etudiant_id = e.id
                LEFT JOIN relecture rr ON rr.exercice_id = x.id
                LEFT JOIN relecture ra ON ra.relecteur_id = e.id
                WHERE e.promotion_id = :promotionId
                GROUP BY e.id, e.nom
                ORDER BY e.id
                """)
                .setParameter("promotionId", promotionId)
                .getResultList();

        return lignes.stream()
                .map(this::versLigne)
                .toList();
    }

    private TableauLigneReponse versLigne(Object[] row) {
        Number id = (Number) row[0];
        String nom = (String) row[1];
        long presences = ((Number) row[2]).longValue();
        long exercices = ((Number) row[3]).longValue();
        BigDecimal moyenne = row[4] == null ? null : new BigDecimal(row[4].toString());
        long attente = ((Number) row[5]).longValue();
        return new TableauLigneReponse(id.longValue(), nom, presences, exercices, moyenne, attente);
    }
}
