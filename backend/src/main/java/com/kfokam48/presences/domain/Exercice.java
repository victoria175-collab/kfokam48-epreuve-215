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
 * Exercice déposé par un étudiant pour une session (EF3) : un seul par session
 * et par auteur (RG6). Le lien n'est jamais un fichier, toujours une URL (RG7).
 * Cycle de vie RG20 / D4 : DEPOSE -> EN_ATTENTE_RELECTURE -> RELU, sans retour.
 */
@Entity
@Table(name = "exercice", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "etudiant_id"}))
public class Exercice {

    public enum Statut {
        DEPOSE, EN_ATTENTE_RELECTURE, RELU
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant auteur;

    @Column(nullable = false, length = 2048)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private Statut statut;

    @Column(name = "depose_at", nullable = false)
    private OffsetDateTime deposeAt;

    @Column(name = "modifie_at")
    private OffsetDateTime modifieAt;

    public Long getId() {
        return id;
    }

    public SessionCours getSession() {
        return session;
    }

    public Etudiant getAuteur() {
        return auteur;
    }

    public String getLien() {
        return lien;
    }

    public Statut getStatut() {
        return statut;
    }

    public OffsetDateTime getDeposeAt() {
        return deposeAt;
    }

    public OffsetDateTime getModifieAt() {
        return modifieAt;
    }

    public void setSession(SessionCours session) {
        this.session = session;
    }

    public void setAuteur(Etudiant auteur) {
        this.auteur = auteur;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public void setDeposeAt(OffsetDateTime deposeAt) {
        this.deposeAt = deposeAt;
    }

    public void setModifieAt(OffsetDateTime modifieAt) {
        this.modifieAt = modifieAt;
    }
}
