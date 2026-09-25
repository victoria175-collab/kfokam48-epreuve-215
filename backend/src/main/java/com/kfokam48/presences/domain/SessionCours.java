package com.kfokam48.presences.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Session de cours : porte le code de presence (RG22, unique), son ouverture et
 * son expiration 15 minutes plus tard (RG1), et la clôture définitive (RG19,
 * clotureeAt NULL tant que la session est ouverte).
 */
@Entity
@Table(name = "session_cours")
public class SessionCours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(name = "ouverture_at", nullable = false)
    private OffsetDateTime ouvertureAt;

    @Column(name = "expiration_at", nullable = false)
    private OffsetDateTime expirationAt;

    @Column(name = "cloturee_at")
    private OffsetDateTime clotureeAt;

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public String getCode() {
        return code;
    }

    public OffsetDateTime getOuvertureAt() {
        return ouvertureAt;
    }

    public OffsetDateTime getExpirationAt() {
        return expirationAt;
    }

    public OffsetDateTime getClotureeAt() {
        return clotureeAt;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOuvertureAt(OffsetDateTime ouvertureAt) {
        this.ouvertureAt = ouvertureAt;
    }

    public void setExpirationAt(OffsetDateTime expirationAt) {
        this.expirationAt = expirationAt;
    }

    public void setClotureeAt(OffsetDateTime clotureeAt) {
        this.clotureeAt = clotureeAt;
    }
}
