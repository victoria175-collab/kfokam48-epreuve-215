package com.kfokam48.presences.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Referentiel charge par la migration de donnees de demonstration (V2, Z4) :
 * l'API n'offre aucune creation ni modification de promotion.
 */
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
}
