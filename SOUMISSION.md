# Soumission — Épreuve finale fullstack KFOKAM48


## Candidat

| | |
|---|---|
| Nom et prénom(s) | MOUTCHEU TCHANDA VICTORIA AUDREY|
| Matricule | 215 |
| Centre | Yaoundé|
| Compte GitHub | victoria175-collab |

## Projet

| | |
|---|---|
| Dépôt (public) | `https://github.com/victoria175-collab/kfokam48-epreuve-215` |
| Commit final — hash complet, 40 caractères | `c80d45c90d6f586fa2163c9c05d0fed8a14aebc6` (merge de la PR #38, version finale v1.0 incluant le jalon `[JALON] v1.0`). Seul le présent fichier est commité après ce hash |
| Branche | `main` |

## Épreuve Git — étape 5

Non applicable — étape non retenue pour cette session.

## Technique

| | |
|---|---|
| Frontend utilisé | React |
| Base de données | PostgreSQL 16 (conteneur Docker), schéma entièrement versionné par les migrations Flyway (V1 à V3) |
| Commandes de démarrage | trois commandes Docker, depuis la racine du dépôt (détail dans le README) : `docker compose up -d db` ; `docker compose up --build backend` ; `docker compose up --build frontend` — puis ouvrir http://localhost:3001 |

## Ce que j'ai livré

Fonctionne : ouverture de session avec code de présence expirant, marquage de la présence par code, dépôt du lien d'exercice, relecture avec note entière et commentaire, tableau récapitulatif du formateur, trois écrans React, erreurs au format imposé, démarrage en trois commandes Docker avec données de démonstration, tests passant par `./mvnw verify` sur poste vierge.

Étape 3 : le bug des présences simultanées est corrigé (issue #33, reproduction par un test qui échoue avant le correctif, PR #35) ; le changement de besoin est livré (issue #34) : chaque exercice est relu par deux pairs distincts, la note retenue est la moyenne des deux, une note seule est affichée marquée comme provisoire. Migration V3 strictement additive : les migrations V1 et V2 déjà poussées ne sont pas modifiées, les données de démonstration survivent.

Volontairement laissé de côté : les issues #12 à #17 (présence ajoutée à la main, clôture de session, remplacement du lien, blocage après cinq codes erronés, détail des présences par session), priorités Should et Could du backlog. Ce sacrifice est assumé pour absorber le changement de besoin tardif de l'étape 3, arrivé en Must : un périmètre réduit et annoncé valait mieux qu'un périmètre complet non tenu. La consultation de la note par l'étudiant (EF12) est livrée côté API et à l'écran, avec la note retenue et son indicateur provisoire.


---

**Déclaration.** J'ai réalisé ce travail seul avec l'IA. Les outils d'IA étaient autorisés sans restriction et je les ai utilisés ; mon journal indique où et comment j'ai vérifié leurs réponses. Mon dépôt restera public et inchangé jusqu'à la publication des résultats.

Signature : _____VICTORIA MOUTCHEU_________________ 

 Date : ___25/09/2026_______

