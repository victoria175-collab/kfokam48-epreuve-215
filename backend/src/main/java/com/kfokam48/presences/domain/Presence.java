package com.kfokam48.presences.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;

/**
 * Présence d'un étudiant à une session, saisie par code (EF2) ou ajoutée par le
 * formateur (EF9, source FORMATEUR). L'unicité (session, étudiant) est garantie
 * par la base (RG3) : un étudiant a au plus une présence par session, quelle que
 * soit sa source.
 */
@Entity
@Table(name = "presence", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "etudiant_id"}))
public class Presence {

    public enum Source {
        ETUDIANT, FORMATEUR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Source source;

    @Column(name = "marquee_at", nullable = false)
    private OffsetDateTime marqueeAt;

    public Long getId() {
        return id;
    }

    public SessionCours getSession() {
        return session;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public Source getSource() {
        return source;
    }

    public OffsetDateTime getMarqueeAt() {
        return marqueeAt;
    }

    public void setSession(SessionCours session) {
        this.session = session;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setMarqueeAt(OffsetDateTime marqueeAt) {
        this.marqueeAt = marqueeAt;
    }
}
