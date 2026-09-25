# Journal de bord — 215

> Une entrée par étape, écrite au moment où l'étape se termine. Trois lignes suffisent.

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges complet (14 EF avec critères d'acceptation, 22 RG sourcées Qx, contradictions C1 à C3 tranchées, zones d'ombre Z1 à Z14 dont le trou Z2 : aucun relecteur disponible quand l'auteur est seul présent). Quatre diagrammes PlantUML (D1 cas d'utilisation, D2 modèle de données aligné sur les migrations prévues, D3 séquence « marquer sa présence » alignée sur les codes du contrat, D4 bonus états-transitions). Contrat d'API complété : les 5 opérations imposées restées telles quelles, 10 opérations ajoutées, figé avant tout commit de code. Product Backlog : 22 issues GitHub (14 Must, 5 Should, 1 Could, 2 Must transverses frontend/Docker), instantané versionné dans docs/BACKLOG.md. Le commit [JALON] analyse contient le cahier des charges, les quatre diagrammes, le contrat et l'instantané du backlog.

**Bloqué :** environ 30 min sur la contradiction Q10 / Q15. Tranchée en faveur de Q15 : le contrat impose `409 RELECTURE_DEJA_RENDUE`, suivre Q10 obligerait à l'enfreindre ou à inventer une opération hors contrat. Deux autres tensions résolues : Q16 (présence « à chaque session ») contre le champ `presences` entier du contrat, couverte par une opération de détail séparée (C2) ; « fin de session » (Q3) contre clôture par le formateur (Q12), résolue en distinguant expiration du code et clôture (C3).

**IA :** l'IA a produit une deuxieme version du cahier des charges, des diagrammes et du découpage en issues. Vérifié en relisant chaque EF et chaque RG contre CLIENT.md et le contrat, en contrôlant que chaque code HTTP des diagrammes figurait bien dans le contrat, et en corrigeant deux écarts trouvés ainsi : le statut 404 de D3 ramené à 400 (le contrat n'admet que 400/409/410 sur POST /api/presences).

---

## Étape 2 — Première version

**Fait :** toutes les stories Must livrées (issues #1 à #11 et #18 à #22), une branche et une pull request par lot de travail, empilées dans l'ordre. Backend : migrations Flyway V1/V2 conformes à D2 avec séquences repositionnées, gestion centralisée des erreurs { code, message }, ouverture de session avec code de 6 caractères (RG22, RG1), listes des écrans (EF8), présences (RG1 à RG3, RG21), dépôt d'exercice (RG6 à RG8, RG21), affectation automatique du relecteur (RG10 à RG12, Z2, reprise à chaque nouvelle présence), relectures (RG13 à RG16, Z11, note décimale refusée sans troncature), tableau par agrégation SQL sans N+1 (RG17, RG18, ENF3). Frontend React : couche api/ unique, trois écrans, erreurs affichées telles que renvoyées, moyenne jamais recalculée (F3). Démarrage en trois commandes Docker documenté. 41 tests passent via ./mvnw verify sur poste vierge (H2 migrée par les mêmes scripts).

**Bloqué :** le jeton GitHub fin-grain n'avait pas la permission « Pull requests » : création impossible via l'API, PR créées et fusionnées à la main à partir des liens fournis. Environ 40 min sur la collision d'identifiants après les insertions explicites de V2 (23505), corrigée en repositionnant les séquences, et une heure sur la validation stricte de la note entière (Jackson tronquait 12.5 en 12) : résolue par lecture JSON brute côté contrôleur, comme prévu en section 8 du cahier des charges.

**IA :** l'IA a écrit le code et les tests, l'historique a été vérifié commit par commit (un fichier de test était glissé dans le mauvais commit, il a été déplacé dans la bonne branche avant fusion) ; le jeu de données du test d'affectation a été corrigé après vérification manuelle des données de démonstration ; le contrat a été relu face aux réponses HTTP réelles (NOTE_INVALIDE plutôt que CORPS_JSON_INVALIDE pour une note décimale).

---

## Étape 3 — Enveloppe

**Fait :** le jalon v0.1 étant poussé, l'enveloppe a été lue en entier avant de toucher au code : un bug (deux présences simultanées, une seule enregistrée) et un changement de besoin (deux relecteurs par exercice, note retenue = moyenne des deux, note seule affichée provisoire). Deux issues ouvertes avant toute correction (#33, #34). Le bug a été reproduit par un test qui échoue d'abord, puis corrigé dans une branche dédiée hotfix/presence-concurrente (PR #35, fusionnée sur main). L'évolution a suivi sur feature/deux-relecteurs : migration V3 additive (limite de deux relectures de rangs distincts remplaçant l'unicité stricte de V1, V1 et V2 jamais modifiées, données de démonstration préservées), contrat d'API mis à jour, service d'affectation porté à deux rangs, passage en RELU seulement après les deux rendus, note retenue (moyenne, indicateur provisoire) exposée par l'API et affichée à l'étudiant, tests d'intégration sur les deux rangs, la moyenne et le provisoire. Analyse remise à jour dans un commit dédié (C4, RG10 réécrite, RG23 ajoutée, EF4/EF6/EF7/EF12 précisées, D1 à D4 corrigés), backlog re-priorisé par écrit (docs/BACKLOG.md et commentaire sur #34).

**Bloqué :** environ 40 minutes sur la migration V3 : la contrainte d'unicité posée en V1 l'a été sans nom explicite, donc générée, différente entre H2 et PostgreSQL ; découverte du nom par les métadonnées JDBC (information_schema) pour rester portable. La contrainte d'unicité stricte interdisait aussi de tester le provisoire avec une seule relecture : contourné par un exercice dont le second rang n'est affecté qu'à la présence suivante.

**IA :** l'IA a écrit le correctif, la migration, les tests et les écrans. Vérifié en relisant la migration contre D2 (l'index (exercice_id, rang) remplace bien l'ancien), en rejouant le test de reproduction avant/après correctif, en contrôlant chaque code HTTP du contrat contre les réponses réelles, et en faisant relire la moyenne : 2 décimales, moitié de la somme des notes entières, jamais recalculée côté front (F3).

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :** les Should/Could restants : #17 abandonnée en premier (Could), puis #12, #13, #14 et #16 sacrifiées — des fonctionnalités complètes qui ne tiennent pas dans le temps restant, alors que le correctif et l'évolution sont tous deux Must et prioritaires. #15 (EF12) a été maintenue et livrée avec #34 car le contrat de l'évolution en définissait déjà la réponse. Le détail est écrit dans docs/BACKLOG.md et en commentaire de l'issue #34 : un périmètre réduit et assumé vaut mieux qu'un périmètre annoncé et non tenu.

---

## Étape 4 — Version finale

**Fait :** CHANGELOG rédigé à partir de l'historique Git réel (analyse, v0.1, correctif #33/PR #35, évolution #34/PR #37). Backlog restant trié et documenté dans docs/BACKLOG.md : #12, #13, #14, #16 et #17 marquées hors périmètre avec la raison écrite, #15 livrée avec l'étape 3. README relu et testé depuis un clone vierge sur GitHub : les trois commandes Docker montent la base, le backend (migrations V1 à V3 et données de démonstration appliquées au démarrage) et le frontend (http://localhost:3001) ; vérifié par API que les données et la nouvelle réponse étudiant (note retenue, indicateur provisoire) sont servies. Descriptions mises à jour (deux relecteurs, note moyenne, provisoire). Journal trié et backlog documenté, jalon v1.0 posé.

**Bloqué :** le port 8080 était occupé par la pile Docker du projet principal pendant le test du clone : backend et frontend du projet principal stoppés pendant la vérification, puis relancés. Un premier démarrage du conteneur backend du clone est resté sans réseau après l'échec de port ; réglé par un `docker compose down` puis un redémarrage propre.

**IA :** l'IA a rédigé le CHANGELOG, relu le README et conduit le test du clone vierge. Vérifié en rejouant les trois commandes du README dans un dossier neuf, en contrôlant les réponses API (promotions, étudiants, exercices d'un étudiant) et en relisant chaque entrée du CHANGELOG contre les messages de commit réels.

---

## Étape 5 — Épreuve Git

Non applicable.

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
