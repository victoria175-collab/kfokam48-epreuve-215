# Cahier des charges — Présences et relectures KFOKAM48

**Auteur :** Moutcheu Victoria · 215
**Version :** 1 · **Date :** 25/09/2026
**Frontend choisi :** React, parce que sa bibliothèque de composants et son outillage (Vite) permettent de livrer trois écrans simples rapidement avec un build vérifiable en une commande.

---

## 1. Contexte et objectif

La direction de la formation KFOKAM48 suit aujourd'hui à la main deux choses : qui assiste aux sessions de cours, et comment les étudiants progressent sur leurs exercices. Les deux sont difficiles à tenir à jour : la feuille de présence circule en salle, et la relecture des exercices entre pairs dépend de la bonne volonté de chacun.

L'application remplace ces pratiques par un outil web unique :

- le formateur ouvre une session et affiche un **code de présence** que les étudiants présents saisissent depuis leur téléphone ;
- chaque étudiant **dépose le lien** de son exercice pour la session ;
- le système **désigne deux relecteurs** parmi les étudiants présents, qui rendent chacun une note et un commentaire ; la note retenue est la moyenne des deux, et une note seule reste affichée, marquée comme provisoire (mise à jour post-enveloppe, C4) ;
- le formateur consulte un **tableau** qui résume, par étudiant, présences, exercices déposés, moyenne des notes reçues et relectures encore dues.

L'objectif est que le formateur sache, sans tenir de registre, qui était là, qui a travaillé, et quelles relectures bloquent.

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| Formateur | Ouvrir une session pour une promotion et obtenir son code (EF1) ; ajouter une présence à la main (EF9) ; clôturer une session (EF10) ; consulter le tableau de sa promotion (EF7, EF14) | Rendre une relecture ; modifier une note ; rouvrir une session clôturée (RG19) |
| Étudiant | Choisir son nom dans une liste (EF8) ; marquer sa présence avec le code (EF2) ; déposer puis remplacer le lien de son exercice (EF3, EF11) ; consulter la note et le commentaire reçus (EF12) | Relire son propre exercice (RG12) ; connaître le nom de son relecteur (RG16) ; marquer sa présence avec un code expiré (RG1) |
| Relecteur | Consulter les relectures qui lui sont affectées (EF5) ; rendre une note et un commentaire, une seule fois (EF6) | Choisir l'exercice qu'il relit (RG11) ; revenir sur une note rendue (RG14) |
| Système | Générer le code (RG22) ; affecter le relecteur au hasard (EF4, RG11) ; bloquer les tentatives abusives (RG4) ; calculer la moyenne (RG17) | — |

**Le relecteur n'est pas un acteur distinct : c'est un étudiant dans un certain rôle, pour un exercice donné.** Un même étudiant est à la fois auteur de son exercice et relecteur de celui d'un pair (Q7 : le relecteur est choisi « parmi les étudiants présents »). Conséquence sur le modèle de données (D2) : il n'existe pas de table `relecteur` ; la table `relecture` porte une clé étrangère `relecteur_id` vers `etudiant`.

Le formateur n'est pas modélisé en base : sans authentification (Q1), rien dans les besoins ne demande de distinguer deux formateurs (voir section 7, point Z3).

## 3. Périmètre

**Inclus dans cette version :**

- ouverture de session avec code de présence expirant (EF1) ;
- marquage de présence par code, avec protection contre la devinette (EF2, EF13) ;
- présence ajoutée par le formateur, signalée comme telle (EF9) ;
- dépôt et remplacement du lien d'un exercice (EF3, EF11) ;
- affectation automatique et aléatoire de deux relecteurs distincts par exercice (EF4, mise à jour post-enveloppe) ;
- relecture avec note entière de 0 à 20 et commentaire (EF5, EF6) ;
- consultation par l'étudiant de sa note et de son commentaire, sans le nom du relecteur (EF12) ;
- clôture de session par le formateur (EF10) ;
- tableau récapitulatif par étudiant pour une promotion (EF7), et détail des présences par session (EF14) ;
- consultation des promotions, étudiants et sessions pour alimenter les listes des écrans (EF8) ;
- données de démonstration chargées au démarrage (ENF6).

**Explicitement exclu :**

- toute authentification, tout mot de passe, toute gestion de comptes (Q1) ;
- la création, la modification et la suppression des promotions et des étudiants : ces référentiels sont fournis par les données de démonstration (voir Z4) ;
- la gestion de plusieurs formateurs et des droits d'accès par formateur (Z3) ;
- la modification d'une note déjà rendue (contradiction Q10/Q15 tranchée en C1) ;
- la réouverture d'une session clôturée ;
- la réaffectation d'un relecteur défaillant (Q11 demande seulement de rendre l'attente visible) ;
- les notifications (courriel, SMS, push) ;
- l'hébergement de l'exercice : l'application ne stocke qu'un lien, jamais un fichier ;
- l'export du tableau (CSV, PDF) ;
- l'application mobile native : l'usage mobile passe par le navigateur (ENF1).

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | Le formateur ouvre une session de cours pour une promotion et obtient un code de présence | Quand j'envoie un titre et une promotion existante, alors je reçois `201` avec un code de 6 caractères, `ouvertureAt` et `expirationAt = ouvertureAt + 15 min`. Quand le titre ou la promotion manque, alors je reçois `400` au format d'erreur imposé | Must |
| EF2 | L'étudiant marque sa présence à l'aide du code | Quand je saisis un code valide et non expiré, alors je reçois `201` avec `source = ETUDIANT` et ma présence est comptée dans le tableau du formateur. Code inconnu : `400 CODE_INCONNU`. Deuxième saisie : `409 DEJA_PRESENT`. Code expiré : `410 CODE_EXPIRE` | Must |
| EF3 | L'étudiant dépose le lien de son exercice pour une session | Quand je dépose un lien `http(s)` valide pour une session non clôturée, alors je reçois `201` avec un identifiant et un statut. Lien mal formé : `400 LIEN_INVALIDE`. Second dépôt pour la même session : `409 EXERCICE_DEJA_DEPOSE` | Must |
| EF4 | Le système affecte automatiquement deux relecteurs distincts à chaque exercice déposé (mise à jour post-enveloppe, issue #34) | Quand un exercice est déposé et qu'au moins deux autres étudiants sont présents à la session, alors deux relectures de rangs distincts sont créées pour deux de ces étudiants, jamais l'auteur, et l'exercice passe en `EN_ATTENTE_RELECTURE`. Quand moins de deux candidats existent, le premier rang est affecté dès qu'un candidat existe, le second à la présence suivante ; un exercice sans relecteur reste `DEPOSE` et l'affectation est retentée à chaque nouvelle présence dans la session. L'exercice passe en `RELU` dès que les deux relectures sont rendues | Must |
| EF5 | Le relecteur consulte les relectures qui lui sont affectées | Quand je choisis mon nom, alors je vois la liste de mes relectures avec le lien de l'exercice, la session et l'état (à rendre / rendue), sans le nom de l'auteur | Must |
| EF6 | Le relecteur rend une note et un commentaire | Quand je rends une note entière entre 0 et 20 et un commentaire, alors je reçois `200` ; quand les deux relecteurs ont rendu, l'exercice passe en `RELU` (avant, il reste `EN_ATTENTE_RELECTURE`). Note hors bornes ou décimale : `400 NOTE_INVALIDE`. Auteur de l'exercice : `403 AUTO_RELECTURE`. Relecture déjà rendue : `409 RELECTURE_DEJA_RENDUE` | Must |
| EF7 | Le formateur consulte le tableau récapitulatif d'une promotion | Quand je demande le tableau d'une promotion existante, alors je reçois `200` avec, pour chaque étudiant : nombre de présences, nombre d'exercices déposés, moyenne des notes reçues, calculée par l'API selon RG23 (`null` si aucune note) et nombre de relectures qu'il doit encore rendre. Promotion inconnue : `404 PROMOTION_INCONNUE` | Must |
| EF8 | Les écrans proposent des listes de promotions, d'étudiants et de sessions | Quand j'ouvre un écran, alors je choisis ma promotion, mon nom et la session dans des listes fournies par l'API, sans rien saisir à la main. Le code de présence n'apparaît dans aucune de ces listes | Must |
| EF9 | Le formateur ajoute une présence à la main | Quand j'ajoute un étudiant de la promotion à une session non clôturée, alors je reçois `201` avec `source = FORMATEUR`, même si le code est expiré, et la mention « ajouté par le formateur » est visible dans le détail de la session | Should |
| EF10 | Le formateur clôture une session | Quand je clôture une session ouverte, alors je reçois `200` et, ensuite, tout dépôt, remplacement de lien, relecture ou ajout de présence sur cette session est refusé par `409 SESSION_CLOTUREE`. Une seconde clôture est refusée par `409 SESSION_CLOTUREE` | Should |
| EF11 | L'étudiant remplace le lien de son exercice | Quand je remplace le lien d'un exercice dont la relecture n'est pas rendue et dont la session n'est pas clôturée, alors je reçois `200` et le nouveau lien est celui que voit le relecteur. Relecture déjà rendue : `409 LIEN_NON_MODIFIABLE` | Should |
| EF12 | L'étudiant consulte la note et le commentaire reçus | Quand je consulte mes exercices, alors je vois pour chacun son statut et, dès qu'au moins une relecture est rendue, la note retenue (RG23) avec son indicateur `noteProvisoire` et le commentaire. Aucune réponse de l'API ne contient l'identité d'un relecteur | Should |
| EF13 | Le système bloque un étudiant après cinq codes erronés | Quand je saisis cinq codes inconnus consécutifs, alors toute nouvelle tentative, même avec un code valide, est refusée par `429 TROP_DE_TENTATIVES` pendant 2 minutes ; après ce délai, je peux de nouveau marquer ma présence | Should |
| EF14 | Le formateur voit la présence de chaque étudiant à chaque session | Quand je consulte le détail d'une promotion, alors je vois, pour chaque étudiant et chaque session, s'il était présent et si la présence a été ajoutée par le formateur | Could |

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'écran étudiant (présence, dépôt) est utilisable sur un téléphone de 360 px de large, sans défilement horizontal | Ouvrir l'écran dans les outils de développement du navigateur en mode 360 x 640 : les champs et boutons sont lisibles et cliquables sans zoom |
| ENF2 | Volumétrie cible : jusqu'à 10 promotions de 60 étudiants, 5 sessions par semaine et par promotion, sur une année | Les données de démonstration et les tests d'intégration utilisent des volumes cohérents ; aucune requête ne charge une table entière en mémoire pour calculer le tableau |
| ENF3 | Le tableau d'une promotion de 60 étudiants répond en moins de 2 s | Le calcul est fait par des requêtes d'agrégation en base (pas de boucle N+1) ; mesure sur les données de démonstration via l'onglet réseau du navigateur |
| ENF4 | Toute erreur renvoyée par l'API respecte le format `{ "code", "message" }`, message en français, sans stack trace | Test d'intégration sur au moins un cas d'erreur ; appel d'une route inexistante et d'un corps JSON mal formé : la réponse reste au format imposé |
| ENF5 | Les dates échangées sont au format ISO 8601 avec fuseau (UTC) | Lecture des réponses de `POST /api/sessions` : `ouvertureAt` et `expirationAt` se terminent par `Z` |
| ENF6 | L'application démarre chez un tiers en trois commandes Docker au plus, avec des données de démonstration (au moins une promotion, des étudiants, une session, un exercice) | Test depuis un clone vierge dans un dossier vide, en suivant uniquement le `README` |
| ENF7 | Le schéma de base est entièrement versionné ; aucune modification manuelle de la base | Une base vide démarrée par Docker atteint le schéma complet par les seules migrations Flyway |
| ENF8 | Le code de présence résiste à la devinette | 6 caractères parmi 32 (plus d'un milliard de combinaisons), combinés au blocage RG4 |

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Un code de présence expire 15 minutes après l'ouverture de la session : `expirationAt = ouvertureAt + 15 min`. À partir de l'instant `expirationAt` inclus, le code est refusé par `410 CODE_EXPIRE` | Q2, contrat |
| RG2 | On ne peut pas marquer sa présence par code après la fin de la session. Le code d'une session clôturée est refusé par `410 CODE_EXPIRE`, même avant 15 minutes | Q3, décision Z1 |
| RG3 | Un étudiant a au plus une présence par session, quelle que soit sa source. Une seconde présence est refusée par `409 DEJA_PRESENT` | Q16, contrat |
| RG4 | Après cinq codes inconnus consécutifs, l'étudiant est bloqué deux minutes : toute tentative est refusée par `429 TROP_DE_TENTATIVES`. Le compteur est remis à zéro par une présence réussie ou à la fin du blocage. Un code expiré ne compte pas comme erreur | Q4, décision Z6 |
| RG5 | Le formateur peut ajouter une présence à la main, même après expiration du code, tant que la session n'est pas clôturée. Cette présence porte `source = FORMATEUR` et s'affiche « ajouté par le formateur » | Q14, contrat |
| RG6 | Un étudiant dépose au plus un exercice par session. Un second dépôt est refusé par `409 EXERCICE_DEJA_DEPOSE` | Contrat |
| RG7 | Un lien d'exercice est une URL absolue en `http` ou `https`, de 2048 caractères au plus. Sinon : `400 LIEN_INVALIDE` | Contrat, décision Z9 |
| RG8 | Le dépôt d'un exercice est possible jusqu'à la clôture de la session, y compris après l'expiration du code | Q12 |
| RG9 | Le lien d'un exercice peut être remplacé tant que sa relecture n'a pas été rendue et que la session n'est pas clôturée. Sinon : `409 LIEN_NON_MODIFIABLE` ou `409 SESSION_CLOTUREE` | Q13, décision Z5 |
| RG10 | Un exercice a au plus deux relecteurs, de rangs distincts : au plus une relecture de rang 1 et une de rang 2, jamais deux relectures du même rang pour un même exercice | Q6, remplacée par l'enveloppe de l'étape 3 (issue #34) |
| RG23 | La note retenue d'un exercice est la moyenne arithmétique des relectures rendues, arrondie à deux décimales. Une seule relecture rendue : sa note est affichée, marquée `noteProvisoire = true`. Les deux rendues : la moyenne est affichée, `noteProvisoire = false`. La note retenue est calculée uniquement par l'API | Enveloppe de l'étape 3 (issue #34) |
| RG11 | Le relecteur est choisi par le système, au hasard, parmi les étudiants présents à la session de l'exercice, auteur exclu. Pour répartir la charge, le tirage se fait parmi les candidats qui ont le moins de relectures affectées dans cette session | Q7, Q5, décision Z2 |
| RG12 | Un étudiant ne relit jamais son propre exercice : l'affectation l'exclut (RG11) et toute tentative de l'auteur est refusée par `403 AUTO_RELECTURE` | Q5, contrat |
| RG13 | Une note est un entier compris entre 0 et 20 inclus. Une valeur décimale, négative, supérieure à 20 ou absente est refusée par `400 NOTE_INVALIDE` | Q9, contrat |
| RG14 | Une relecture rendue est définitive : une seconde soumission est refusée par `409 RELECTURE_DEJA_RENDUE` | Q15, contrat, contradiction C1 |
| RG15 | Aucune relecture ne peut être rendue sur une session clôturée : `409 SESSION_CLOTUREE`. L'exercice concerné reste « en attente » | Q10, Q11, décision Z7 |
| RG16 | L'auteur d'un exercice voit la note et le commentaire reçus, jamais l'identité de son relecteur. Aucune réponse de l'API destinée à l'étudiant ne contient `relecteurId` | Q8 |
| RG17 | La moyenne d'un étudiant dans le tableau est celle de ses notes retenues (RG23), arrondie à deux décimales. Elle vaut `null` si aucune note n'a été reçue. Elle est calculée uniquement par l'API | Q16, contrainte F3, précisée par l'enveloppe (issue #34) |
| RG18 | Un exercice dont la relecture n'est pas rendue reste « en attente de relecture », y compris après la clôture de la session, et apparaît dans le compteur `relecturesEnAttente` de son relecteur | Q11, Q16 |
| RG19 | Seul le formateur clôture une session ; la clôture est définitive | Q10, Q12, décision Z8 |
| RG20 | Le statut d'un exercice suit le cycle `DEPOSE` → `EN_ATTENTE_RELECTURE` → `RELU` (diagramme D4). Aucun retour arrière n'est possible | Q11, Q15, D4 |
| RG21 | Un étudiant n'agit que sur les sessions de sa propre promotion. Le code d'une session d'une autre promotion est traité comme inconnu (`400 CODE_INCONNU`) ; un dépôt sur une session d'une autre promotion est refusé par `403 ETUDIANT_HORS_PROMOTION` | Décision Z4 |
| RG22 | Le code de présence est une chaîne de 6 caractères majuscules et chiffres, sans caractères ambigus (0, O, 1, I), unique parmi toutes les sessions | Décision Z10, ENF8 |

## 7. Zones d'ombre, hypothèses et contradictions

**Contradictions relevées :**

| Réf | Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|---|
| C1 | **Q10** : « Un relecteur peut-il corriger sa note après l'avoir envoyée ? — Oui, tant que le formateur n'a pas clôturé la session. » **Q15** : « La note est-elle définitive une fois envoyée ? — Oui. Une fois que le relecteur a validé, c'est fini, il ne peut plus y revenir. » | **Q15 l'emporte : une relecture rendue est définitive (RG14).** Q10 est écartée ; la seule chose que conserve la notion de clôture de Q10 est la fin de la période où une relecture peut être rendue (RG15) | Le contrat d'API imposé tranche de lui-même : `POST /api/relectures/{id}` doit répondre `409` quand « la relecture est déjà rendue ». Suivre Q10 obligerait soit à violer ce code imposé, soit à inventer une opération de correction que le contrat ne prévoit pas. Q15 est de plus justifiée par le client (« plus honnête pour tout le monde »), alors que Q10 est une permission sans motif |
| C2 | **Q16** demande de voir « sa présence **à chaque session** » ; le contrat impose pour `GET /api/tableau` un champ `presences` de type **entier** | Le tableau imposé renvoie `presences` = **nombre de sessions** auxquelles l'étudiant a été présent. Le détail session par session est fourni par une opération supplémentaire (EF14, `GET /api/promotions/{id}/presences`) | Le contrat est imposé « exactement ainsi » ; la demande Q16 est couverte sans le modifier, par une opération ajoutée |
| C3 | **Q3** parle de « la fin de la session », **Q12** distingue « la fin de la session » de la clôture par le formateur (« jusqu'à ce que je clôture ») | Deux notions distinctes : la fin du marquage de présence est l'expiration du code (RG1) ou la clôture si elle arrive avant (RG2) ; la clôture est un acte explicite du formateur (EF10) | Aucune réponse ne donne d'heure de fin de cours ; l'expiration à 15 minutes (Q2) intervient toujours avant la fin réelle du cours, donc Q3 est satisfaite par RG1 sans modéliser une heure de fin |
| C4 | **Enveloppe de l'étape 3** contre **Q6** (« un seul relecteur ») et partiellement contre **Q15** (« la note est définitive une fois envoyée ») : « chaque exercice est relu par deux pairs différents, la note retenue est la moyenne des deux ; si un seul a rendu, on affiche sa note en attendant, mais marquée comme provisoire » | Le client a tranché lui-même : RG10 est réécrite (au plus deux relecteurs de rangs distincts), la règle RG23 introduit la note retenue (moyenne des rendues, une note seule affichée provisoire). Q15 reste vraie pour chaque relecture prise isolément — une relecture rendue n'est jamais modifiable (RG14) — c'est la note affichée à l'étudiant qui devient évolutive jusqu'au second rendu | Demande explicite du client dans l'enveloppe, prioritaire sur toute réponse antérieure de CLIENT.md ; traitée comme un Must tardif (issue #34), au prix d'un sacrifice de périmètre écrit (voir journal et backlog) |

**Points que la demande ne tranche pas (le trou principal est Z2) :**

| Réf | Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|---|
| Z1 | Que devient le code si le formateur clôture la session avant 15 minutes ? | Q2 et Q3 ne couvrent pas ce cas | Le code est refusé par `410 CODE_EXPIRE` dès la clôture (RG2) | Une seule réponse d'erreur pour « le code ne marche plus », conforme au contrat |
| **Z2** | **Trou non vu par le client : que se passe-t-il si aucun relecteur n'est disponible ?** Q7 impose un relecteur « parmi les étudiants présents », Q5 exclut l'auteur, Q12 permet de déposer après la session. Si l'auteur est seul présent, ou dépose avant que d'autres aient marqué leur présence, aucune affectation n'est possible. Rien ne dit non plus si un même étudiant peut recevoir toutes les relectures | Aucune question ne couvre ce cas | L'exercice reste `DEPOSE` (non affecté). L'affectation est retentée automatiquement à chaque nouvelle présence enregistrée dans la session (par code ou par le formateur). Le tirage se fait parmi les présents les moins chargés dans la session (RG11) | Le statut `DEPOSE` existe réellement et se distingue d'`EN_ATTENTE_RELECTURE` (D4). Aucune relecture ne peut être « orpheline » ; un exercice jamais affecté est visible dans le détail |
| Z3 | Faut-il plusieurs formateurs, des comptes ? | Q1 : pas de mot de passe | Un seul rôle formateur, non modélisé en base, sans authentification. L'écran formateur est accessible à tous | Hors périmètre (section 3). Risque accepté : un étudiant averti pourrait appeler les opérations du formateur ; le code n'est exposé que dans la réponse d'ouverture de session et dans le détail d'une session |
| Z4 | Qui crée les promotions et les listes d'étudiants que Q1 suppose ? | Q1 parle d'une « liste » sans dire d'où elle vient ; le contrat utilise `promotionId` sans définir la promotion | Les promotions et étudiants sont des référentiels chargés par migration de données de démonstration ; l'API les expose en lecture seule (EF8). Un étudiant appartient à une seule promotion | Pas d'écran d'administration. RG21 empêche d'agir sur une autre promotion |
| Z5 | Que signifie « commencé à relire » (Q13) ? | L'API n'a aucun événement « début de relecture » : la relecture est rendue en un seul appel | Le lien est modifiable tant que la relecture n'est pas rendue (RG9) | Le relecteur voit toujours le lien courant ; aucun champ supplémentaire à gérer |
| Z6 | Q4 : cinq erreurs sur quelle période, et bloquer quoi ? | Q4 : « au bout de cinq erreurs, bloquez-le deux minutes » | Cinq codes inconnus **consécutifs** pour un même étudiant ; blocage de l'étudiant (pas de l'appareil, qui n'est pas identifiable) pendant 2 minutes, avec `429 TROP_DE_TENTATIVES`. Un code expiré n'est pas une devinette et ne compte pas | Code `429` ajouté au contrat pour ce cas nouveau, sans toucher aux codes imposés pour les autres cas. Table `tentative_code` dans D2 |
| Z7 | Peut-on rendre une relecture après la clôture ? | Q10 et Q11 le laissent entendre sans le dire | Non : `409 SESSION_CLOTUREE` (RG15). L'exercice reste « en attente » et visible (Q11) | La clôture fige la moyenne de la session |
| Z8 | Qui clôture, et peut-on rouvrir ? | Q10, Q12 : « je clôture » | Le formateur seul, définitivement (RG19) | Opération `POST /api/sessions/{id}/cloture` ajoutée ; pas d'opération de réouverture |
| Z9 | Qu'est-ce qu'un « lien invalide » (contrat) ? | Contrat : `format: uri` | URL absolue `http` ou `https`, 2048 caractères au plus (RG7) | Validation côté API ; le front affiche le message renvoyé |
| Z10 | Format du code de présence ? | Non précisé ; Q4 craint la devinette | 6 caractères parmi 32 symboles non ambigus, unique (RG22) | Contrainte d'unicité en base |
| Z11 | Le corps imposé de `POST /api/relectures/{id}` ne dit pas **qui** rend la relecture ; comment déclencher `403 AUTO_RELECTURE` ? | Q1 : l'étudiant choisit son nom, sans mot de passe | L'identité déclarée est transmise dans l'en-tête facultatif `X-Etudiant-Id`. S'il désigne l'auteur : `403 AUTO_RELECTURE` ; s'il désigne un autre étudiant que le relecteur affecté : `403 RELECTEUR_NON_AFFECTE`. S'il est absent, l'appel est réputé venir du relecteur affecté, qui par construction n'est jamais l'auteur (RG11) | Le corps imposé n'est pas modifié ; le frontend envoie toujours l'en-tête |
| Z12 | Un étudiant absent peut-il déposer un exercice ? | Q12 autorise le dépôt après la session, sans condition de présence | Oui : présence et dépôt sont deux indicateurs indépendants du tableau (Q16). Seule la relecture exige d'avoir été présent (Q7) | L'absent peut être relu mais ne relit pas |
| Z13 | Une relecture affectée dans une session clôturée compte-t-elle encore dans « relectures qu'il doit encore faire » (Q16) ? | Q11 : le formateur doit voir clairement ce qui est en attente | Oui, elle reste comptée dans `relecturesEnAttente` (RG18) | Le formateur repère les relecteurs défaillants |
| Z14 | L'auteur peut-il connaître son relecteur, et le relecteur l'auteur ? | Q8 interdit seulement le premier | Aucune des deux identités n'est exposée aux étudiants | Réponses de l'API sans `relecteurId` ni nom d'auteur côté relecteur |

**Réponses sans effet sur la conception :** Q1 (pas d'authentification) ne réclame aucun développement ; Q6 (un seul relecteur) se traduisait par une simple contrainte d'unicité — cette réponse est caduque depuis l'enveloppe de l'étape 3 (contradiction C4).

## 8. Contraintes techniques

Contraintes imposées par le sujet :

| Réf | Contrainte |
|---|---|
| B1 | Java 17 ou plus, Maven, wrapper `mvnw` commité |
| B2 | Contrat `api/contrat.yaml` respecté à la lettre : chemins, verbes, codes de statut, format d'erreur |
| B3 | Séparation contrôleur / service / repository ; aucune requête en base dans un contrôleur ; DTO en frontière d'API, aucune entité JPA sérialisée |
| B4 | Validation des entrées (Bean Validation) et gestion centralisée des erreurs par `@RestControllerAdvice` ; aucune stack trace renvoyée au client |
| B5 | Schéma versionné par Flyway, migrations commitées ; `ddl-auto=update` interdit hors tests |
| B6 | Au moins un test unitaire sur une règle métier réelle et un test d'intégration sur un endpoint, exécutables sur un poste vierge |
| F1 | Framework déclaré et justifié en une ligne dans le `README` ; le build passe |
| F2 | Trois écrans : formateur, étudiant, relecteur |
| F3 | Appels API dans une couche dédiée ; états de chargement et d'erreur gérés ; aucune règle métier dupliquée (la moyenne vient de l'API) |

Choix complémentaires :

- **Base de données :** PostgreSQL 16, dans un conteneur Docker.
- **Migrations :** Flyway, une migration par évolution, jamais de modification d'une migration déjà poussée ; les données de démonstration sont une migration distincte. Le SQL reste compatible PostgreSQL et H2 (identité `GENERATED BY DEFAULT AS IDENTITY`, `TIMESTAMP WITH TIME ZONE`).
- **Tests :** JUnit 5 et Mockito pour les règles métier ; `@SpringBootTest` + MockMvc sur une base H2 en mémoire en mode PostgreSQL, migrée par les mêmes scripts Flyway, pour que les tests tournent sans base locale ni Docker.
- **Horloge injectable** (`java.time.Clock`) dans les services pour tester l'expiration du code (RG1) et le blocage (RG4) sans attente réelle.
- **Désérialisation stricte :** refus des nombres décimaux dans un champ entier (RG13), pour qu'une note `12.5` ne soit jamais tronquée silencieusement en `12`.
- **Frontend :** React avec Vite ; une couche `api/` unique qui encapsule `fetch` et transforme les erreurs au format imposé.
- **Démarrage :** trois services Docker (base, backend, frontend), chacun démarré par sa propre commande documentée dans le `README`.

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` : ce document, tenu à jour après l'étape 3.
- `docs/diagrammes/` : D1 cas d'utilisation, D2 modèle de données, D3 séquence « marquer sa présence », D4 cycle de vie d'un exercice, en PlantUML.
- `docs/JOURNAL.md` : une entrée par étape.
- `api/contrat.yaml` : les cinq opérations imposées et les opérations ajoutées, figé avant le premier commit de code.
- Backlog : issues GitHub avec critères d'acceptation, priorité Must / Should / Could et renvoi aux EF / RG.
- `backend/` : application Spring Boot avec migrations Flyway et tests.
- `frontend/` : application React, trois écrans.
- `README.md` : installation et démarrage en trois commandes Docker, testés depuis un clone vierge.
- `CHANGELOG.md` : cohérent avec l'historique Git.
- Trois commits jalons : `[JALON] analyse`, `[JALON] v0.1`, `[JALON] v1.0`.

## 10. Démarche prévue

La démarche suit Scrum, adaptée à une journée :

- **Product Backlog :** les issues du dépôt, une par exigence (EF) ou exigence technique (ENF), ordonnées par priorité Must, puis Should, puis Could.
- **Sprint 1 (étape 2, v0.1) :** uniquement les issues Must. Chaque issue est traitée sur sa propre branche (`feature/EFx-description`), fusionnée par pull request dans `main`, le commit de fusion fermant l'issue. `main` reste compilable et testé à chaque fusion.
- **Revue de sprint :** à la fin de chaque étape, l'incrément est inspecté avant de poser le jalon et de pousser.
- **Étape 3 :** ouverture de l'enveloppe ; une issue pour le bug, une pour l'évolution, avant toute ligne de code. Reproduction du bug documentée dans l'issue, nouvelle migration Flyway, mise à jour du contrat, puis de ce document et des diagrammes dans un commit dédié, puis re-priorisation écrite du backlog.
- **Sprint 2 (étape 4, v1.0) :** issues Should, puis Could, dans la limite du temps. En cas de retard, les Could sont abandonnées en premier, puis les Should, et l'abandon est écrit dans le backlog : la propreté de l'historique et la justesse de l'analyse passent avant la complétude.
- **Journal :** une entrée dans `docs/JOURNAL.md` à la fin de chaque étape, dans un commit distinct.

**Definition of Done — un ticket est terminé quand :**

- chaque critère d'acceptation de l'issue est vérifié, et les règles de gestion citées sont couvertes par un test automatisé ou, à défaut, par une vérification manuelle décrite dans la pull request ;
- les opérations touchées respectent `api/contrat.yaml` : chemin, verbe, codes de statut et format d'erreur ;
- aucune règle métier n'est dupliquée côté frontend : le front affiche ce que renvoie l'API ;
- toute modification de schéma passe par une nouvelle migration Flyway ;
- `./mvnw verify` et `npm run build` passent ;
- la branche est fusionnée dans `main` par une pull request qui référence l'issue, et l'issue est fermée.

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 25/09/2026 | Version initiale (étape 1) |
| 2 | 25/09/2026 | Mise à jour post-enveloppe (étape 3) : deux relecteurs par exercice et note retenue (C4, RG10 réécrite, RG23, EF4/EF6/EF7/EF12 précisées) |
