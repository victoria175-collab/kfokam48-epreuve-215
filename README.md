# Présences et relectures — KFOKAM48 (matricule 215)

Application web pour la direction de la formation KFOKAM48 : le formateur ouvre
une session et affiche un code de présence, les étudiants présents le saisissent
depuis leur téléphone, déposent le lien de leur exercice, et un relecteur est
désigné au hasard parmi les présents. Le formateur consulte un tableau
récapitulatif (présences, exercices déposés, moyenne des notes reçues,
relectures encore dues).

Le frontend est en React : son outillage (Vite) a permis de livrer trois écrans
simples rapidement avec un build vérifiable en une commande.

## Démarrage en trois commandes Docker

Depuis la racine du dépôt, après avoir cloné :

```
docker compose up -d db
docker compose up --build backend
docker compose up --build frontend
```

1. La première commande démarre la base PostgreSQL 16.
2. La deuxième construit et démarre le backend Spring Boot : le schéma et les
   données de démonstration sont créés par les migrations Flyway au démarrage.
3. La troisième construit et démarre le frontend (nginx) : ouvrez
   http://localhost:3001

À la fin, `Ctrl+C` arrête les services lancés au premier plan ; `docker compose down`
arrête tout (ajoutez `-v` pour repartir d'une base vide).

Les données de démonstration sont chargées automatiquement : une promotion de
sept étudiants, une session de cours ouverte (code `KD2M4A`, valable 15 minutes
après le démarrage du backend — ouvrez une nouvelle session depuis l'écran
formateur si elle a expiré), trois étudiants présents, un exercice déposé avec
une relecture en attente (connectez-vous en tant que « Bertrand Etoundi » sur
l'écran relecteur).

La configuration passe par des variables d'environnement (`DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USER`, `DB_PASSWORD`) définies dans `docker-compose.yml` ; aucun
secret n'est commité dans le code.

## Tests

```
cd backend
./mvnw verify
```

Les tests s'exécutent sur une base H2 en mémoire migrée par les mêmes scripts
Flyway : aucun poste de développement ni base locale n'est requis.

```
cd frontend
npm install
npm run build
```

## Structure

```
api/        contrat d'API OpenAPI (les cinq opérations imposées + les opérations ajoutées)
backend/    Spring Boot (Java 17, Maven, wrapper mvnw), migrations Flyway, tests
frontend/   React (Vite), couche api/ dédiée, trois écrans
docs/       cahier des charges, journal, diagrammes PlantUML, instantané du backlog
```

La séparation contrôleur / service / repository est stricte, les entités JPA ne
sortent jamais en JSON (DTO en frontière), les erreurs répondent toutes au
format `{ "code", "message" }`, et aucune règle métier n'est dupliquée côté
frontend : la moyenne vient de l'API.
