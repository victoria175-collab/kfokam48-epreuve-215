# Product Backlog — kfokam48-epreuve-215

Ce document est l'instantané du Product Backlog contenu dans le jalon `[JALON] analyse`. Chaque issue vit sur GitHub (le numéro y fait foi) ; ce fichier en fige le contenu au moment du jalon : titre orienté résultat, priorité Scrum (Must / Should / Could), renvoi aux exigences fonctionnelles (EFx), aux règles de gestion (RGx) et aux contraintes techniques, et critères d'acceptation vérifiables.

Les issues Must forment le Sprint 1 (étape 2, version v0.1). Les Should et Could restent en backlog pour le sprint 2 (étape 4) et seront re-priorisées par écrit après l'ouverture de l'enveloppe (étape 3). La Definition of Done appliquée à chaque story est celle de la section 10 du cahier des charges.

**Re-priorisation post-enveloppe (étape 3) :** elle est écrite en fin de document. Le bug signalé par le client (#33) et le nouveau Must (#34) ont été traités en priorité, dans deux branches et deux pull requests séparées, au prix du sacrifice des issues #12, #13, #14, #16 et #17 ; la story #15 (EF12) est livrée dans la foulée de #34.

---

## Vue d'ensemble

| # | Titre | Priorité | Sprint | Renvoi |
|---|---|---|---|---|
| [#1](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/1) | Ouvrir une session et obtenir un code de présence | Must | 1 | EF1 (RG1, RG22, RG19) |
| [#2](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/2) | Marquer sa présence avec le code | Must | 1 | EF2 (RG1, RG2, RG3, RG21) |
| [#3](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/3) | Déposer le lien de son exercice | Must | 1 | EF3 (RG6, RG7, RG8, RG21) |
| [#4](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/4) | Affecter automatiquement un relecteur à chaque exercice déposé | Must | 1 | EF4 (RG10, RG11, RG12, Z2) |
| [#5](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/5) | Consulter les relectures qui me sont affectées | Must | 1 | EF5 (RG16) |
| [#6](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/6) | Rendre une note et un commentaire | Must | 1 | EF6 (RG12, RG13, RG14, RG15) |
| [#7](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/7) | Consulter le tableau récapitulatif d'une promotion | Must | 1 | EF7 (RG17, RG18) |
| [#8](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/8) | Fournir les listes promotions, étudiants et sessions aux écrans | Must | 1 | EF8 (Z3, Z4) |
| [#9](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/9) | Gestion centralisée des erreurs au format imposé | Must | 1 | ENF4, B2, B4 |
| [#10](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/10) | Versionner le schéma par Flyway et charger les données de démonstration | Must | 1 | ENF6, ENF7, B5, Z4 |
| [#11](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/11) | Tests exécutables sur un poste vierge | Must | 1 | B6, ENF6 |
| [#12](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/12) | Ajouter une présence à la main, signalée « ajouté par le formateur » | Should | hors périmètre (étape 3) | EF9 (RG5, RG3) |
| [#13](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/13) | Clôturer une session, définitivement | Should | hors périmètre (étape 3) | EF10 (RG15, RG19) |
| [#14](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/14) | Remplacer le lien de son exercice | Should | hors périmètre (étape 3) | EF11 (RG9) |
| [#15](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/15) | Consulter sa note et son commentaire, sans le nom du relecteur | Should | 3 (livrée avec #34) | EF12 (RG16, Z14, RG23) |
| [#16](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/16) | Bloquer un étudiant après cinq codes erronés | Should | hors périmètre (étape 3) | EF13 (RG4, Z6) |
| [#17](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/17) | Voir la présence de chaque étudiant à chaque session | Could | hors périmètre (étape 3) | EF14 (C2) |
| [#33](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/33) | Bug : deux présences simultanées, une seule enregistrée | Must | 3 (étape 3) | Correctif RG3, test de reproduction |
| [#34](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/34) | Chaque exercice est relu par deux pairs, note retenue = moyenne des deux | Must | 3 (étape 3) | RG10 réécrite, RG23, C4, V3 |
| [#18](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/18) | Écran formateur : ouvrir une session et voir le tableau | Must | 1 | F2, F1, F3 |
| [#19](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/19) | Écran étudiant : marquer sa présence et déposer son exercice | Must | 1 | F2, F3, ENF1 |
| [#20](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/20) | Écran relecteur : consulter et rendre une relecture | Must | 1 | F2, F3 |
| [#21](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/21) | Couche d'appel API dédiée du frontend | Must | 1 | F3 |
| [#22](https://github.com/victoria175-collab/kfokam48-epreuve-215/issues/22) | Démarrage en trois commandes Docker avec données de démonstration | Must | 1 | ENF6, ENF7 |

---

## Sprint 1 — v0.1 (issues Must)

### #1 — Ouvrir une session et obtenir un code de présence (EF1)

Sources : Q2, contrat. Règles : RG1, RG22, RG19.

- POST /api/sessions avec titre et promotionId valides répond 201 avec id, code à 6 caractères, ouvertureAt et expirationAt = ouvertureAt + 15 min.
- Titre ou promotionId manquant : 400 au format { code, message }.
- Promotion inconnue : 404 PROMOTION_INCONNUE.
- Le code est unique parmi toutes les sessions (RG22) et apparaît dans le détail de la session.

### #2 — Marquer sa présence avec le code (EF2)

Sources : Q2, Q3, Q16, contrat. Règles : RG1, RG2, RG3, RG21.

- Code valide et non expiré : 201 avec id, sessionId, etudiantId, source = ETUDIANT.
- Code inconnu : 400 CODE_INCONNU.
- Deuxième présence du même étudiant dans la même session : 409 DEJA_PRESENT (RG3).
- Code expiré (après expirationAt, inclus) : 410 CODE_EXPIRE (RG1).
- Code d'une session clôturée : 410 CODE_EXPIRE, même avant 15 min (RG2).
- Code d'une session d'une autre promotion : 400 CODE_INCONNU (RG21).
- Une présence réussie déclenche la ré-affectation des exercices en attente (Z2, EF4).

### #3 — Déposer le lien de son exercice (EF3)

Source : Q12, contrat. Règles : RG6, RG7, RG8, RG21.

- POST /api/exercices avec lien http(s) valide : 201 avec id et statut DEPOSE ou EN_ATTENTE_RELECTURE.
- Lien mal formé, non http(s) ou de plus de 2048 caractères : 400 LIEN_INVALIDE (RG7).
- Second dépôt pour la même session : 409 EXERCICE_DEJA_DEPOSE (RG6).
- Dépôt possible après expiration du code, jusqu'à la clôture (RG8, Q12).
- Dépôt sur une session d'une autre promotion : 403 ETUDIANT_HORS_PROMOTION (RG21).

### #4 — Affecter automatiquement un relecteur à chaque exercice déposé (EF4)

Sources : Q5, Q6, Q7. Règles : RG10, RG11, RG12, décision Z2.

- Un exercice déposé quand au moins un autre étudiant est présent crée exactement une relecture (RG10, Q6) pour un étudiant présent, jamais l'auteur (RG11, RG12, Q5, Q7).
- Le tirage se fait parmi les présents les moins chargés en relectures dans la session (RG11).
- Aucun candidat disponible : l'exercice reste DEPOSE et l'affectation est retentée à chaque nouvelle présence (Z2).
- L'exercice passe en EN_ATTENTE_RELECTURE dès qu'un relecteur est affecté.
- Test unitaire sur la règle de tirage (exclusion de l'auteur, moins chargé en premier).

### #5 — Consulter les relectures qui me sont affectées (EF5)

Sources : Q8, Q11. Règle : RG16.

- GET /api/relectures?relecteurId= renvoie la liste des relectures de l'étudiant avec lien de l'exercice, session et état (à rendre / rendue).
- Aucune réponse ne contient le nom de l'auteur de l'exercice (RG16).
- Écran relecteur alimenté par cette opération uniquement.

### #6 — Rendre une note et un commentaire (EF6)

Sources : Q5, Q9, Q10, Q15, contrat. Règles : RG12, RG13, RG14, RG15.

- POST /api/relectures/{id} avec note entière 0..20 et commentaire : 200, l'exercice passe en RELU.
- Note hors bornes ou décimale : 400 NOTE_INVALIDE (RG13, Q9).
- Auteur de l'exercice : 403 AUTO_RELECTURE (RG12, Q5) ; identification par en-tête X-Etudiant-Id (décision Z11).
- Relecture déjà rendue : 409 RELECTURE_DEJA_RENDUE (RG14, Q15 — contradiction C1 tranchée contre Q10).
- Session clôturée : 409 SESSION_CLOTUREE (RG15).
- La relecture non rendue reste comptée dans relecturesEnAttente (RG18).

### #7 — Consulter le tableau récapitulatif d'une promotion (EF7)

Sources : Q11, Q16, contrainte F3. Règles : RG17, RG18.

- GET /api/tableau?promotionId= répond 200 avec, pour chaque étudiant : etudiantId, nom, presences (entier, C2), exercicesDeposes, moyenne, relecturesEnAttente.
- Moyenne calculée par l'API uniquement (RG17, F3), arrondie à 2 décimales, null si aucune note.
- Relectures affectées non rendues comptées, y compris après clôture (RG18, Q11).
- Promotion inconnue : 404 PROMOTION_INCONNUE.
- Calcul par agrégation en base, sans boucle N+1 (ENF3).

### #8 — Fournir les listes promotions, étudiants et sessions aux écrans (EF8)

Décisions : Z3, Z4.

- GET /api/promotions, GET /api/promotions/{id}/etudiants et GET /api/sessions?promotionId= alimentent les listes des trois écrans.
- Aucune saisie manuelle d'identifiant dans les écrans.
- Le code de présence n'apparaît pas dans les listes ; il n'est exposé que dans le détail de session (GET /api/sessions/{id}, Z3).

### #9 — Gestion centralisée des erreurs au format imposé (ENF4, B2, B4)

- Un @RestControllerAdvice unique : toute erreur répond { code, message } en français, sans stack trace.
- Route inexistante et corps JSON mal formé : même format imposé.
- Codes d'erreur du contrat respectés à la lettre (B2) ; test d'intégration sur au moins un cas d'erreur.

### #10 — Versionner le schéma par Flyway et charger les données de démonstration (ENF6, ENF7, B5, Z4)

- Migration initiale V1 créant promotion, etudiant, session_cours, presence, exercice, relecture, tentative_code (conforme à D2).
- Migration de données de démonstration distincte : au moins une promotion, des étudiants, une session, un exercice.
- Une base vide atteint le schéma complet par les seules migrations ; ddl-auto=update interdit hors tests.
- D2 correspond exactement aux migrations.

### #11 — Tests exécutables sur un poste vierge (B6, ENF6)

- Un test unitaire sur une règle métier réelle (expiration du code RG1 ou tirage RG11).
- Un test d'intégration sur un endpoint, sur H2 en mémoire migrée par les mêmes scripts Flyway.
- ./mvnw verify passe sur un poste vierge, sans base locale ni Docker.

### #18 — Écran formateur : ouvrir une session et voir le tableau (F2, F1, F3)

- L'écran propose la liste des promotions (EF8), la création d'une session (EF1) et affiche le code de présence reçu.
- Le tableau de la promotion (EF7) est affiché : presences, exercicesDeposes, moyenne, relecturesEnAttente, tels que renvoyés par l'API.
- La moyenne n'est jamais recalculée côté client (F3, RG17).
- États de chargement et d'erreur gérés (F3) ; erreur affichée au format { code, message } reçu.

### #19 — Écran étudiant : marquer sa présence et déposer son exercice (F2, F3, ENF1)

- Choix de la promotion, de son nom (EF8) et de la session dans des listes fournies par l'API.
- Saisie du code et marquage de présence (EF2) ; les messages d'erreur CODE_INCONNU, DEJA_PRESENT, CODE_EXPIRE sont affichés tels que renvoyés par l'API.
- Dépôt du lien d'exercice (EF3) avec confirmation ; erreur LIEN_INVALIDE affichée telle quelle.
- Utilisable à 360 px de large sans défilement horizontal (ENF1).
- États de chargement et d'erreur gérés (F3).

### #20 — Écran relecteur : consulter et rendre une relecture (F2, F3)

- Choix de son nom dans la liste (EF8), consultation des relectures à rendre (EF5) : lien de l'exercice, session, état.
- Rendu d'une note entière 0..20 et d'un commentaire (EF6) ; le nom de l'auteur n'apparaît nulle part (RG16).
- Erreurs NOTE_INVALIDE, AUTO_RELECTURE, RELECTURE_DEJA_RENDUE affichées telles que renvoyées par l'API.
- États de chargement et d'erreur gérés (F3).

### #21 — Couche d'appel API dédiée du frontend (F3)

- Un seul module frontend/api/ encapsule tous les appels ; aucun fetch hors de ce module.
- Les erreurs HTTP sont converties en objet { code, message } unique pour toute l'application.
- Aucune règle métier dupliquée côté client : la moyenne, les statuts et les messages viennent de l'API.

### #22 — Démarrage en trois commandes Docker avec données de démonstration (ENF6, ENF7)

- Le README documente trois commandes : base de données, backend, frontend (ou services séparés), testé depuis un clone vierge.
- Données de démonstration chargées au démarrage : au moins une promotion, des étudiants, une session, un exercice (EF8, Z4).
- Aucun secret commité ; configuration par variables d'environnement documentées.

---

## Backlog — sprint 2 (Should, Could)

### #12 — Ajouter une présence à la main, signalée « ajouté par le formateur » (EF9) — Should

Source : Q14. Règles : RG5, RG3.

- POST /api/sessions/{id}/presences : 201 avec source = FORMATEUR, même si le code est expiré.
- Session clôturée : 409 SESSION_CLOTUREE ; déjà présent : 409 DEJA_PRESENT.
- La mention « ajouté par le formateur » est visible dans le détail de la session.

### #13 — Clôturer une session, définitivement (EF10) — Should

Sources : Q10, Q12. Règles : RG15, RG19.

- POST /api/sessions/{id}/cloture : 200 avec clotureeAt.
- Après clôture : dépôt, remplacement de lien, relecture et ajout de présence refusés par 409 SESSION_CLOTUREE.
- Seconde clôture : 409 SESSION_CLOTUREE ; pas d'opération de réouverture (RG19).

### #14 — Remplacer le lien de son exercice (EF11) — Should

Source : Q13, décision Z5. Règle : RG9.

- PUT /api/exercices/{id} avec lien valide : 200, le relecteur voit le nouveau lien.
- Relecture rendue : 409 LIEN_NON_MODIFIABLE ; session clôturée : 409 SESSION_CLOTUREE.
- Lien invalide : 400 LIEN_INVALIDE (RG7).

### #15 — Consulter sa note et son commentaire, sans le nom du relecteur (EF12) — Should

Source : Q8. Règles : RG16, Z14.

- GET /api/etudiants/{id}/exercices : statut de chaque exercice, note et commentaire si relu.
- Aucune réponse destinée à l'étudiant ne contient relecteurId (RG16, Z14).

### #16 — Bloquer un étudiant après cinq codes erronés (EF13) — Should

Source : Q4. Règles : RG4, Z6.

- Cinq codes inconnus consécutifs : toute nouvelle tentative répond 429 TROP_DE_TENTATIVES pendant 2 minutes.
- Après le délai ou une présence réussie, le compteur est remis à zéro.
- Un code expiré ne compte pas comme erreur ; blocage par étudiant, pas par appareil (Z6).

### #17 — Voir la présence de chaque étudiant à chaque session (EF14) — Could

Source : Q16, contradiction C2.

- GET /api/promotions/{id}/presences : une ligne par étudiant et par session, avec present (booléen) et source (ETUDIANT / FORMATEUR / null).
- Promotion inconnue : 404 PROMOTION_INCONNUE.

---

## Re-priorisation après l'étape 3

L'enveloppe a apporté deux sujets nouveaux, tous deux Must et prioritaires sur tout le reste :

**1. Le correctif (issue #33, branche hotfix/presence-concurrente, PR #35) — livré.**
Deux présences saisies simultanément pouvaient n'en enregistrer qu'une. Il a été traité avant l'évolution : un service indisponible fait perdre des présences réelles, alors que l'évolution attend une heure. Reproduction par un test qui échoue d'abord, correctif ensuite, dans une branche dédiée, séparée de l'évolution.

**2. L'évolution de besoin (issue #34, branche feature/deux-relecteurs, PR dédiée) — livrée.**
Deux relecteurs distincts par exercice, note retenue = moyenne des deux, une note seule affichée provisoire. Migration V3 additive (V1 et V2 inchangées), contrat mis à jour, analyse remise à jour dans un commit dédié, tests d'intégration couvrant les deux rangs, la moyenne et le provisoire.

**Ce qui sort du périmètre, et pourquoi.** Le nouveau Must arrive tard dans la journée. Pour l'absorber sans mettre en risque l'existant, les stories Should et Could restantes sont sacrifiées :

- **#12** (présence ajoutée à la main, EF9) — sacrifiée : l'ouverture du périmètre RG3 par le correctif rendrait sa reprise tentante, mais elle reste une fonctionnalité complète (écran, source FORMATEUR, mention visible) qui ne tient pas dans le temps restant.
- **#13** (clôture de session, EF10) — sacrifiée : RG15 est déjà garantie par construction (aucune relecture après clôture possible tant que la clôture elle-même n'existe pas) ; l'absence de l'écran ne crée pas d'incohérence.
- **#14** (remplacement du lien, EF11) — sacrifiée : RG9 reste codée côté API (409 LIEN_NON_MODIFIABLE couvert par les tests), seule l'opération est absente.
- **#16** (blocage après cinq codes erronés, EF13) — sacrifiée : RG4 décrite dans l'analyse, non développée ; l'application reste cohérente sans elle (le tableau et la présence ne dépendent pas du compteur).
- **#17** (détail des présences par session, EF14, Could) — abandonnée en premier, comme prévu à la section 10 du cahier des charges.

**Ce qui est maintenu :** la story **#15** (EF12), Should, est livrée dans la foulée de #34 — le contrat de l'évolution définit précisément la réponse attendue par l'étudiant (note retenue, indicateur provisoire, commentaires), la livrer ne coûtait qu'un contrôleur, un service et un écran déjà en place.

Un périmètre réduit et assumé vaut mieux qu'un périmètre annoncé et non tenu : le reste du temps disponible est consacré à la solidité de l'analyse, à la propreté de l'historique et à la vérification du démarrage depuis un clone vierge (étape 4).
