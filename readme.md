# MTG Deck Assistant

API backend en **Java / Spring Boot** pour gérer des decks **Magic: The Gathering**.

Le projet permet de créer et gérer des decks, importer des decklists, valider certaines règles de format, récupérer des données depuis **Scryfall**, calculer des statistiques de deck et générer des suggestions de cartes avec **Google Gemini**.

---

## Stack

- **Java 25**
- **Spring Boot 4.1.1**
  - Spring Web
  - Validation
  - Spring Data JPA
  - Spring Boot Actuator
- **PostgreSQL 18**
- **Hibernate / JPA**
- **Flyway**
- **Caffeine Cache**
- **Scryfall API**
- **Google Gemini API**
- **Jackson 3**
- **Maven / Maven Wrapper**
- **Docker**
- **Docker Compose**
- **PowerShell** pour les scripts de développement

---

## Fonctionnalités

### Gestion des decks

- Création d’un deck
- Consultation d’un deck
- Modification du nom et du format
- Suppression d’un deck
- Suivi de la date de création et de dernière modification

### Gestion des cartes

- Ajout d’une carte dans un deck
- Mise à jour de la quantité
- Suppression d’une carte
- Gestion des doublons et quantités selon les règles du format
- Gestion spécifique des terrains de base en Commander

### Import de decklists

Deux formats d’import sont disponibles :

- JSON : `POST /decks/{id}/import`
- Plain text : `POST /decks/{id}/import-text`

L’import retourne notamment :

- `addedSlots`
- `updatedSlots`
- `duplicateLines`
- `ignoredLines`
- `invalidLines`

### Gestion du commandant

Pour les decks Commander :

- Définir un commandant
- Consulter le commandant actuel
- Retirer le commandant
- Vérifier que le commandant appartient bien au deck
- Vérifier qu’il n’existe qu’en un seul exemplaire

### Validation de deck

Un endpoint permet de vérifier certaines contraintes du deck :

- taille du deck
- doublons interdits
- présence d’un commandant pour les decks Commander
- cohérence du commandant avec le deck

### Statistiques

Les statistiques d’un deck incluent :

- nombre total de cartes
- nombre de terrains
- nombre de créatures
- nombre d’artefacts
- nombre d’enchantements
- nombre d’éphémères
- nombre de rituels
- nombre de planeswalkers
- CMC moyen des cartes non-terrain
- mana curve
- cartes non retrouvées sur Scryfall

### Intégration Scryfall

L’API utilise Scryfall pour :

- rechercher une carte par nom exact
- effectuer des recherches Scryfall
- récupérer plusieurs cartes en collection
- récupérer les informations nécessaires aux statistiques
- récupérer les cartes proposées par l’assistant

Les réponses Scryfall sont mises en cache avec **Caffeine**.

### Suggestions assistées par Gemini

Pour un deck Commander disposant d’un commandant, Gemini peut générer plusieurs requêtes Scryfall adaptées au deck.

Le backend :

- récupère les informations du commandant
- génère des requêtes Scryfall avec Gemini
- nettoie et valide les requêtes retournées
- applique les contraintes de légalité Commander
- exécute les recherches sur Scryfall
- retire les cartes déjà présentes dans le deck
- retourne une liste de suggestions avec previews

---

## Prérequis

Pour lancer le projet en local :

- **Java 25**
- **Docker Desktop**
- **Maven Wrapper** fourni avec le projet

PostgreSQL peut être lancé directement via Docker Compose.

---

## Variables d’environnement

Le projet utilise des variables d’environnement pour les informations sensibles.

Variables nécessaires :

```text
POSTGRES_USER=...
POSTGRES_PASSWORD=...
GEMINI_API_KEY=...
```

Ces valeurs ne doivent pas être versionnées.

---

## Configuration Spring Boot

Configuration principale :

```properties
spring.application.name=mtg-deck-assistant

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5433/mtg_deck_assistant}
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false

spring.flyway.enabled=true

spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=5000,expireAfterWrite=7d

gemini.apiKey=${GEMINI_API_KEY}
gemini.model=gemini-3.1-flash-lite

management.endpoints.web.exposure.include=health
```

Le paramètre `DB_URL` permet d’utiliser la même application dans deux contextes :

- depuis Windows / IntelliJ : `localhost:5433`
- depuis Docker Compose : `db:5432`

---

# Docker

Le projet est entièrement exécutable avec Docker Compose.

## Services

Deux services principaux sont définis dans `compose.yml` :

```text
db
└── PostgreSQL 18

mtg-assistant-app
└── Backend Spring Boot
```

Le backend communique avec PostgreSQL via le réseau interne Docker Compose :

```text
jdbc:postgresql://db:5432/mtg_deck_assistant
```

Depuis Windows, PostgreSQL est accessible sur :

```text
localhost:5433
```

L’API Spring Boot est accessible sur :

```text
http://localhost:8080
```

---

## Persistance PostgreSQL

Les données PostgreSQL sont stockées dans un volume Docker nommé :

```text
mtg_deck_assistant_data
```

Un simple :

```bash
docker compose down
```

supprime les conteneurs et le réseau Compose, mais **conserve les données PostgreSQL**.

Pour supprimer volontairement le volume et repartir avec une base vide :

```bash
docker compose down -v
```

À utiliser uniquement lorsqu’un reset complet de la base est souhaité.

---

# Healthchecks

Le projet utilise plusieurs niveaux de vérification de santé.

## PostgreSQL

Le service PostgreSQL utilise `pg_isready`.

Docker ne considère la base comme `healthy` que lorsqu’elle accepte réellement les connexions.

Le backend utilise :

```yaml
depends_on:
  db:
    condition: service_healthy
```

Ainsi, Spring Boot n’est lancé qu’une fois PostgreSQL prêt.

## Backend Spring Boot

Le projet utilise **Spring Boot Actuator**.

Endpoint :

```text
GET /actuator/health
```

Exemple de réponse :

```json
{
  "status": "UP"
}
```

Le backend Docker possède également un healthcheck HTTP basé sur cet endpoint.

L’image Docker installe `curl` afin que le conteneur puisse effectuer ce contrôle.

## Vérifier manuellement

```bash
docker compose ps
```

Lorsque tout fonctionne correctement, les deux services doivent apparaître comme :

```text
healthy
```

Exemple :

```text
mtg-deck-assistant-app   Up (...) (healthy)
mtg-deck-assistant-db    Up (...) (healthy)
```

---

# Dockerfile

Le backend est construit à partir du JAR généré par Maven.

Flux de build :

```text
Code Java
   ↓
Maven package
   ↓
JAR dans target/
   ↓
docker build
   ↓
Image mtg-deck-assistant:latest
```

Le Dockerfile utilise une image Java 25 et installe `curl` pour le healthcheck applicatif.

Une modification du code Java nécessite donc :

1. un nouveau `mvn package`
2. un nouveau `docker build`
3. la recréation du conteneur backend

Les scripts de développement automatisent ce workflow.

---

# Scripts de développement

Le dossier :

```text
scripts/
```

contient plusieurs scripts PowerShell facilitant le lancement et l’arrêt de l’environnement.

Structure :

```text
scripts/
├── common.ps1
├── start-dev.ps1
├── restart-dev.ps1
└── stop-dev.ps1
```

---

## `common.ps1`

Contient les fonctions partagées par les autres scripts.

Principales responsabilités :

- positionnement automatique à la racine du projet
- affichage du banner
- affichage de l’action en cours
- gestion des étapes `[x/y]`
- vérification du Docker daemon
- démarrage automatique de Docker Desktop si nécessaire
- attente de l’état `healthy` des services Docker
- vérification de `/actuator/health`
- affichage des logs backend en cas d’échec

Principales fonctions :

```text
Show-Banner
Show-Action
Check-Step
Ensure-DockerRunning
Wait-ForServicesHealthy
Wait-ForApi
```

Les fonctions d’attente acceptent des paramètres optionnels permettant de modifier le nombre de tentatives, les intervalles et les timeouts.

---

## `start-dev.ps1`

Script principal de développement.

Commande :

```powershell
.\scripts\start-dev.ps1
```

Il effectue automatiquement :

```text
Docker daemon
      ↓
Maven clean
      ↓
Maven package
      ↓
Docker build
      ↓
Docker Compose up --force-recreate
      ↓
Docker services healthy
      ↓
Actuator /health = UP
```

Exemple de sortie :

```text
[1/7] Docker is running ✔
[2/7] Maven clean success ✔
[3/7] Maven package success ✔
[4/7] Docker build success ✔
[5/7] Docker compose up success ✔
[6/7] Docker services healthy ✔
[7/7] API health check UP ✔

MTG Deck Assistant is up and running 🎉 🔥
API        : http://localhost:8080
PostgreSQL : localhost:5433
```

Le script vérifie également `JAVA_HOME` avant de lancer Maven.

---

## `restart-dev.ps1`

Permet de redémarrer rapidement le backend sans rebuild du projet.

Commande :

```powershell
.\scripts\restart-dev.ps1
```

Il effectue :

```text
Docker daemon
      ↓
Restart du service mtg-assistant-app
      ↓
Actuator /health = UP
```

Ce script **ne reconstruit pas l’image Docker**.

Il est donc utile lorsque :

- le backend doit simplement être redémarré
- Spring Boot semble bloqué
- une reconnexion à la base est souhaitée
- aucun changement de code n’a été effectué

Si le code Java a changé, utiliser `start-dev.ps1`.

Exemple :

```text
[1/3] Docker is running ✔
[2/3] Backend restart success ✔
[3/3] API health check UP ✔

MTG Deck Assistant restarted ♻️
```

---

## `stop-dev.ps1`

Arrête proprement l’environnement Docker.

Commande :

```powershell
.\scripts\stop-dev.ps1
```

Le script :

- vérifie que Docker est disponible
- exécute `docker compose down`
- vérifie que les conteneurs ont bien été supprimés
- confirme que les données PostgreSQL sont conservées

Exemple :

```text
[1/2] Docker compose down success ✔
[2/2] Containers stopped and removed ✔
PostgreSQL data preserved ✔

MTG Deck Assistant stopped 🛑
```

Le volume PostgreSQL n’est pas supprimé.

---

# Base de données

Le projet utilise **PostgreSQL** comme base persistante.

## Gestion du schéma avec Flyway

La structure de la base n’est pas modifiée automatiquement par Hibernate.

Hibernate utilise :

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Il vérifie uniquement que le schéma PostgreSQL correspond aux entités JPA.

Les évolutions du schéma sont gérées avec **Flyway**.

Les migrations se trouvent dans :

```text
src/main/resources/db/migration
```

Convention de nommage :

```text
V1__init_schema.sql
V2__add_something.sql
V3__add_index.sql
```

Flyway exécute automatiquement les migrations manquantes dans l’ordre des versions au démarrage.

La première migration du projet est :

```text
V1__init_schema.sql
```

Elle crée notamment :

- `decks`
- `deck_slots`

Flyway conserve l’historique des migrations dans :

```text
flyway_schema_history
```

La base locale historique a été baselinée en version `1`.

Sur une base Docker vide, Flyway exécute directement `V1__init_schema.sql` depuis zéro.

À partir de maintenant, toute évolution du schéma doit être ajoutée dans une **nouvelle migration Flyway**. Une migration déjà appliquée ne doit pas être modifiée.

---

# Démarrage manuel

Le script `start-dev.ps1` est la méthode recommandée en développement.

Il reste néanmoins possible de lancer le backend manuellement.

Sous Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

Ou :

```powershell
.\mvnw.cmd clean package
```

Puis :

```powershell
docker build -t mtg-deck-assistant .
docker compose up -d
```

L’API est disponible sur :

```text
http://localhost:8080
```

Au démarrage :

1. PostgreSQL démarre
2. Docker attend que PostgreSQL soit `healthy`
3. Spring Boot démarre
4. Flyway valide l’historique des migrations
5. Flyway applique les migrations manquantes
6. Hibernate valide le schéma
7. Actuator expose le healthcheck
8. Docker marque le backend comme `healthy`

---

# API

Base URL locale :

```text
http://localhost:8080
```

## Decks

### Créer un deck

```http
POST /decks
```

```json
{
  "name": "Ezio",
  "format": "COMMANDER"
}
```

### Récupérer un deck

```http
GET /decks/{id}
```

### Modifier un deck

```http
PATCH /decks/{id}
```

```json
{
  "name": "Ezio Updated",
  "format": "COMMANDER"
}
```

### Supprimer un deck

```http
DELETE /decks/{id}
```

Retour :

```text
204 No Content
```

---

## Cartes

### Ajouter une carte

```http
POST /decks/{id}/cards
```

```json
{
  "cardName": "Sol Ring",
  "qty": 1
}
```

### Lister les cartes

```http
GET /decks/{id}/cards
```

### Modifier la quantité d’une carte

```http
PATCH /decks/{id}/cards/{slotId}
```

```json
{
  "qty": 2
}
```

La quantité doit être supérieure ou égale à `1`.

### Supprimer une carte

```http
DELETE /decks/{id}/cards/{slotId}
```

---

## Import

### Import JSON

```http
POST /decks/{id}/import
```

### Import plain text

```http
POST /decks/{id}/import-text
```

Header :

```text
Content-Type: text/plain
```

Exemple :

```text
1 Sol Ring
2 Island
1 Command Tower
```

---

## Commander

### Définir un commandant

```http
PUT /decks/{id}/commander
```

### Récupérer le commandant

```http
GET /decks/{id}/commander
```

### Retirer le commandant

```http
DELETE /decks/{id}/commander
```

---

## Validation

```http
GET /decks/{id}/validate
```

---

## Statistiques

```http
GET /decks/{id}/stats
```

---

## Suggestions

```http
POST /decks/{id}/suggestions
```

---

# Endpoints Scryfall

### Rechercher une carte exacte

```http
GET /scryfall/named?name=Sol%20Ring
```

### Rechercher des cartes

```http
GET /scryfall/search
```

Paramètres principaux :

```text
query
order
limit
page
```

### Récupérer une collection de cartes

```http
POST /scryfall/collection
```

---

# Monitoring

## Healthcheck applicatif

```http
GET /actuator/health
```

Réponse attendue :

```json
{
  "status": "UP"
}
```

Cet endpoint est utilisé par :

- Docker
- `start-dev.ps1`
- `restart-dev.ps1`

---

# Gestion des erreurs

Le backend utilise un `@RestControllerAdvice` pour centraliser certaines erreurs HTTP.

Principaux statuts :

- **400 Bad Request**
- **404 Not Found**
- **409 Conflict**
- **429 Too Many Requests**
- **502 Bad Gateway**
- **500 Internal Server Error**

---

# Architecture

```text
HTTP
 ↓
Controller
 ↓
Service
 ↓
Repository / Integration
 ↓
PostgreSQL / Scryfall / Gemini
```

Les entités JPA ne sont pas directement exposées dans les réponses API.

Les controllers retournent des DTO dédiés.

---

# Tests

Le projet dispose actuellement du support de test fourni par Spring Boot.

La prochaine étape est d’ajouter progressivement :

- tests unitaires avec **JUnit**
- mocks avec **Mockito**
- tests des services métier
- tests des controllers
- tests d’intégration
- **Testcontainers** avec PostgreSQL pour reproduire le comportement réel de la base

Les tests seront ensuite intégrés au workflow de développement avant le build Docker.

---

# Roadmap

- [x] CRUD des decks et cartes
- [x] PostgreSQL persistant
- [x] Flyway
- [x] Docker
- [x] Docker Compose
- [x] Scripts PowerShell de développement
- [x] Healthchecks PostgreSQL
- [x] Spring Boot Actuator
- [x] Healthcheck backend Docker
- [ ] Tests backend plus complets
- [ ] Testcontainers avec PostgreSQL
- [ ] Frontend
- [ ] Intégration du frontend au workflow Docker
- [ ] Authentification / utilisateurs
- [ ] Amélioration des statistiques
- [ ] Amélioration des suggestions Gemini
