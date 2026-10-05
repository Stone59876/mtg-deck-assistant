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
- **PostgreSQL**
- **Hibernate / JPA**
- **Flyway**
- **Caffeine Cache**
- **Scryfall API**
- **Google Gemini API**
- **Jackson 3**
- **Maven**

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

- Java 25
- PostgreSQL
- Maven ou Maven Wrapper

---

## Configuration

Le projet utilise des variables d’environnement pour les informations sensibles.

Variables nécessaires :

```text
POSTGRES_USER=...
POSTGRES_PASSWORD=...
GEMINI_API_KEY=...
```

Configuration principale :

```properties
spring.application.name=mtg-deck-assistant

spring.datasource.url=jdbc:postgresql://localhost:5432/mtg_deck_assistant
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false

spring.flyway.enabled=true

spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=5000,expireAfterWrite=7d

gemini.apiKey=${GEMINI_API_KEY}
gemini.model=gemini-3.1-flash-lite
```

---

## Base de données

Le projet utilise **PostgreSQL** comme base de données persistante.

Les données restent disponibles entre les redémarrages de l’application.

### Gestion du schéma avec Flyway

La structure de la base n’est plus modifiée automatiquement par Hibernate.

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

Flyway exécute automatiquement les migrations manquantes dans l’ordre des versions au démarrage de l’application.

La première migration du projet est :

```text
V1__init_schema.sql
```

Elle contient la création du schéma initial avec les tables :

- `decks`
- `deck_slots`

Flyway conserve l’historique des migrations appliquées dans la table :

```text
flyway_schema_history
```

La base locale existante a été baselinée en version `1`, afin de rattacher le schéma déjà existant à l’historique Flyway.

À partir de maintenant, toute évolution du schéma doit être ajoutée dans une nouvelle migration Flyway plutôt que dans une migration déjà appliquée.

---

## Démarrage

Depuis la racine du projet :

```bash
./mvnw spring-boot:run
```

Sous Windows :

```bash
mvnw.cmd spring-boot:run
```

L’API démarre par défaut sur :

```text
http://localhost:8080
```

Au démarrage :

1. Spring Boot se connecte à PostgreSQL
2. Flyway valide l’historique des migrations
3. Flyway applique les nouvelles migrations si nécessaire
4. Hibernate valide le schéma
5. L’application démarre

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

Retour : `204 No Content`

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
Controller
    ↓
Service
    ↓
Repository / Integration
    ↓
PostgreSQL / Scryfall / Gemini
```

Les entités JPA ne sont pas directement exposées dans les réponses API : les controllers retournent des DTO dédiés.

---

# Roadmap

- Docker Compose
- Frontend
- Authentification / utilisateurs
- Amélioration des statistiques
- Amélioration des suggestions Gemini
- Tests automatisés plus complets
- Testcontainers avec PostgreSQL pour les tests d’intégration
