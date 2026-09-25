package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.RegleMetierException;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EF1 : le formateur ouvre une session et obtient un code de presence.
 * RG1 : expiration 15 minutes apres l'ouverture. RG22 : code de 6 caracteres
 * parmi 32 symboles non ambigus, unique parmi toutes les sessions.
 */
@Service
public class SessionService {

    static final Duration DUREE_CODE = Duration.ofMinutes(15);

    // 32 symboles sans 0, O, 1, I (RG22, decision Z10, ENF8).
    private static final String SYMBOLES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGUEUR_CODE = 6;

    private final SessionCoursRepository sessions;
    private final PromotionRepository promotions;
    private final Clock clock;
    private final SecureRandom aleatoire = new SecureRandom();

    public SessionService(SessionCoursRepository sessions, PromotionRepository promotions, Clock clock) {
        this.sessions = sessions;
        this.promotions = promotions;
        this.clock = clock;
    }

    @Transactional
    public SessionCours ouvrir(String titre, long promotionId) {
        Promotion promotion = promotions.findById(promotionId)
                .orElseThrow(() -> new RegleMetierException("PROMOTION_INCONNUE", HttpStatus.NOT_FOUND,
                        "La promotion demandée n'existe pas."));

        OffsetDateTime ouverture = OffsetDateTime.now(clock);
        SessionCours session = new SessionCours();
        session.setTitre(titre);
        session.setPromotion(promotion);
        session.setCode(genererCodeUnique());
        session.setOuvertureAt(ouverture);
        session.setExpirationAt(ouverture.plus(DUREE_CODE));
        return sessions.save(session);
    }

    private String genererCodeUnique() {
        for (int essai = 0; essai < 50; essai++) {
            String code = genererCode();
            if (!sessions.existsByCode(code)) {
                return code;
            }
        }
        throw new RegleMetierException("ERREUR_INTERNE", HttpStatus.INTERNAL_SERVER_ERROR,
                "Impossible de générer un code de présence unique.");
    }

    private String genererCode() {
        StringBuilder sb = new StringBuilder(LONGUEUR_CODE);
        for (int i = 0; i < LONGUEUR_CODE; i++) {
            sb.append(SYMBOLES.charAt(aleatoire.nextInt(SYMBOLES.length())));
        }
        return sb.toString();
    }
}
