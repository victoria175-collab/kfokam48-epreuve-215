package com.kfokam48.presences.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Relecture d'un exercice par un étudiant présent. Depuis V3 (issue #34,
 * enveloppe étape 3) : chaque exercice est relu par deux pairs différents,
 * identifiés par rang (1 et 2). La note (RG13, entier 0..20) et le commentaire
 * restent NULL tant que la relecture n'est pas rendue ; rendue_at pose la fin
 * du cycle (RG14 : une relecture rendue est définitive). L'exercice passe en
 * RELU quand les deux rangs sont rendus.
 */
@Entity
@Table(name = "relecture")
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercice_id", nullable = false, unique = true)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    /** RG13 : entier 0..20, NULL tant que la relecture n'est pas rendue. */
    @Column
    private Short note;

    @Column(length = 2000)
    private String commentaire;

    @Column(name = "affectee_at", nullable = false)
    private OffsetDateTime affecteeAt;

    /** Rang de l'affectation : 1 ou 2 (issue #34, contrainte uq_relecture_exercice_rang). */
    @Column(nullable = false)
    private Short rang;

    @Column(name = "rendue_at")
    private OffsetDateTime rendueAt;

    public Long getId() {
        return id;
    }

    public Exercice getExercice() {
        return exercice;
    }

    public Etudiant getRelecteur() {
        return relecteur;
    }

    public Short getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public OffsetDateTime getAffecteeAt() {
        return affecteeAt;
    }

    public OffsetDateTime getRendueAt() {
        return rendueAt;
    }

    public Short getRang() {
        return rang;
    }

    public void setRang(Short rang) {
        this.rang = rang;
    }

    public void setExercice(Exercice exercice) {
        this.exercice = exercice;
    }

    public void setRelecteur(Etudiant relecteur) {
        this.relecteur = relecteur;
    }

    public void setNote(Short note) {
        this.note = note;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public void setAffecteeAt(OffsetDateTime affecteeAt) {
        this.affecteeAt = affecteeAt;
    }

    public void setRendueAt(OffsetDateTime rendueAt) {
        this.rendueAt = rendueAt;
    }
}
