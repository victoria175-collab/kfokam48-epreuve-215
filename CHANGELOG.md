# CHANGELOG — kfokam48-epreuve-215

Toutes les évolutions notables de ce projet sont documentées ici, dans l'ordre inverse de l'historique Git. Les numéros d'issues et de pull requests renvoient au dépôt.

## [1.0.0] — 25/09/2026 (étape 3 et 4)

### Ajouté

- Deux relecteurs distincts par exercice, affectés parmi les étudiants présents (RG10 réécrite, RG11, RG12) ; l'exercice passe en `RELU` seulement après les deux rendus (issue #34, PR #37).
- Note retenue = moyenne des relectures rendues, arrondie à deux décimales ; une note seule est affichée marquée comme provisoire (RG23, issue #34, PR #37).
- `GET /api/etudiants/{id}/exercices` : l'étudiant consulte le statut de ses exercices, la note retenue, son indicateur provisoire et les commentaires, sans jamais voir l'identité d'un relecteur (EF12, issue #15, PR #37).
- Écran étudiant complété : section « Mes exercices et mes notes » avec badge de statut et mention « provisoire » (issue #15, PR #37).
- Migration Flyway V3 (`V3__deux_relecteurs.java`) : au plus deux relectures par exercice, de rangs distincts (contrainte `UNIQUE (exercice_id, rang)`). Migration strictement additive : V1 et V2 ne sont pas modifiées, les données existantes survivent (issue #34, PR #37).
- Contrat d'API (`api/contrat.yaml`) mis à jour : réponse de détail étudiant avec `note` et `noteProvisoire` (issue #34, PR #37).
- Analyse remise à jour post-enveloppe : contradiction C4 documentée, EF4/EF6/EF7/EF12 précisées, RG10 réécrite, RG23 ajoutée, diagrammes D1 à D4 corrigés (PR #37).
- Backlog re-priorisé par écrit après l'ouverture de l'enveloppe (`docs/BACKLOG.md`, commentaire sur l'issue #34) : périmètre réduit et assumé.
- `SOUMISSION.md` complété (base de données, commandes de démarrage, périmètre livré).

### Corrigé

- Deux présences saisies simultanément pouvaient n'en enregistrer qu'une seule : la présence est désormais enregistrée sous verrou, le doublon est rejeté proprement et la ré-affectation des relecteurs reste cohérente (issue #33, reproduction par un test qui échoue avant le correctif, PR #35).

## [0.1.0] — 25/09/2026 (étape 2, jalon [JALON] v0.1)

### Ajouté

- Ouverture d'une session de cours avec code de présence de 6 caractères, expirant 15 minutes après l'ouverture (EF1, RG1, RG22).
- Marquage de présence par code : code inconnu `400 CODE_INCONNU`, déjà présent `409 DEJA_PRESENT`, code expiré `410 CODE_EXPIRE` (EF2, RG1 à RG3, RG21).
- Dépôt du lien d'un exercice pour une session, avec validation du lien et refus du second dépôt (EF3, RG6 à RG8).
- Affectation automatique d'un relecteur au hasard parmi les présents, auteur exclu, ré-affectation retentée à chaque nouvelle présence quand aucun candidat n'existe (EF4, RG10 à RG12, Z2).
- Consultation des relectures affectées et rendu d'une note entière de 0 à 20 avec commentaire : `400 NOTE_INVALIDE`, `403 AUTO_RELECTURE`, `409 RELECTURE_DEJA_RENDUE`, `409 SESSION_CLOTUREE` (EF5, EF6, RG13 à RG15).
- Tableau récapitulatif d'une promotion par agrégation SQL, sans N+1 : présences, exercices déposés, moyenne calculée par l'API, relectures en attente (EF7, RG17, RG18, ENF3).
- Listes des promotions, étudiants et sessions pour alimenter les écrans (EF8).
- Gestion centralisée des erreurs au format imposé `{ code, message }`, sans stack trace (ENF4, B4).
- Schéma entièrement versionné par migrations Flyway (V1 : schéma, V2 : données de démonstration) (ENF7, B5).
- Frontend React : trois écrans (formateur, étudiant, relecteur), couche d'appel API dédiée, états de chargement et d'erreur, aucune règle métier dupliquée (F1 à F3).
- Démarrage en trois commandes Docker (base PostgreSQL 16, backend, frontend) documenté dans le `README`, avec données de démonstration (ENF6).
- 41 tests automatisés (unitaires et d'intégration) exécutables sur un poste vierge via `./mvnw verify` (B6).

## [Analyse] — 25/09/2026 (étape 1, jalon [JALON] analyse)

### Ajouté

- Cahier des charges complet : 14 exigences fonctionnelles avec critères d'acceptation, 8 exigences non fonctionnelles, 22 règles de gestion sourcées question par question, contradictions C1 à C3 tranchées et zones d'ombre Z1 à Z14 documentées.
- Quatre diagrammes PlantUML : cas d'utilisation, modèle de données aligné sur les migrations, séquence « marquer sa présence » alignée sur les codes HTTP du contrat, cycle de vie d'un exercice (bonus).
- Contrat d'API OpenAPI complété : les cinq opérations imposées plus les opérations nécessaires aux exigences retenues, figé avant le premier commit de code.
- Product Backlog en issues GitHub : 22 stories priorisées Must / Should / Could, chacune renvoyant aux exigences et règles de gestion, avec critères d'acceptation vérifiables.
