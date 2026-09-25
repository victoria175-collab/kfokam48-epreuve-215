# Journal de bord — 215

> Une entrée par étape, écrite au moment où l'étape se termine. Trois lignes suffisent.

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges complet (14 EF avec critères d'acceptation, 22 RG sourcées Qx, contradictions C1 à C3 tranchées, zones d'ombre Z1 à Z14 dont le trou Z2 : aucun relecteur disponible quand l'auteur est seul présent). Quatre diagrammes PlantUML (D1 cas d'utilisation, D2 modèle de données aligné sur les migrations prévues, D3 séquence « marquer sa présence » alignée sur les codes du contrat, D4 bonus états-transitions). Contrat d'API complété : les 5 opérations imposées restées telles quelles, 10 opérations ajoutées, figé avant tout commit de code. Product Backlog : 22 issues GitHub (14 Must, 5 Should, 1 Could, 2 Must transverses frontend/Docker), instantané versionné dans docs/BACKLOG.md. Le commit [JALON] analyse contient le cahier des charges, les quatre diagrammes, le contrat et l'instantané du backlog.

**Bloqué :** environ 30 min sur la contradiction Q10 / Q15. Tranchée en faveur de Q15 : le contrat impose `409 RELECTURE_DEJA_RENDUE`, suivre Q10 obligerait à l'enfreindre ou à inventer une opération hors contrat. Deux autres tensions résolues : Q16 (présence « à chaque session ») contre le champ `presences` entier du contrat, couverte par une opération de détail séparée (C2) ; « fin de session » (Q3) contre clôture par le formateur (Q12), résolue en distinguant expiration du code et clôture (C3).

**IA :** l'IA a produit une deuxieme version du cahier des charges, des diagrammes et du découpage en issues. Vérifié en relisant chaque EF et chaque RG contre CLIENT.md et le contrat, en contrôlant que chaque code HTTP des diagrammes figurait bien dans le contrat, et en corrigeant deux écarts trouvés ainsi : le statut 404 de D3 ramené à 400 (le contrat n'admet que 400/409/410 sur POST /api/presences).

---

## Étape 2 — Première version

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 3 — Enveloppe

**Fait :**

**Bloqué :**

**IA :**

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Épreuve Git

Non applicable.

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
