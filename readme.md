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

## Lancer le projet

### Prérequis

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

Configuration actuelle de la datasource :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mtg_deck_assistant
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
```

Le modèle Gemini utilisé est configuré dans `application.properties`.

---

## Base de données

Le projet utilise **PostgreSQL** comme base persistante.

La structure de la base est actuellement gérée automatiquement par Hibernate avec :

```properties
spring.jpa.hibernate.ddl-auto=update
```

Les données persistent donc entre les redémarrages de l’application.

À terme, la gestion du schéma sera déplacée vers **Flyway**.

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

---

# API

Base URL locale :

```text
http://localhost:8080
```

---

## Decks

### Créer un deck

```http
POST /decks
```

Exemple :

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

Exemple :

```json
{
  "name": "Ezio Updated",
  "format": "COMMANDER"
}
```

Les champs sont optionnels.

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

Exemple :

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

Exemple :

```json
{
  "qty": 2
}
```

La quantité doit être supérieure ou égale à `1`.

Pour retirer complètement une carte du deck, utiliser l’endpoint `DELETE`.

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

Exemple :

```json
{
  "decklist": "1 Sol Ring\n2 Island\n1 Command Tower",
  "mergeDuplicates": true
}
```

### Import plain text

```http
POST /decks/{id}/import-text
```

Header :

```text
Content-Type: text/plain
```

Body :

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

Exemple :

```json
{
  "cardName": "The Ur-Dragon"
}
```

Le commandant doit :

- être présent dans le deck
- avoir une quantité égale à `1`
- être défini sur un deck au format `COMMANDER`

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

### Valider un deck

```http
GET /decks/{id}/validate
```

La réponse contient notamment :

- le format
- le nombre total de cartes
- le statut de validation
- les problèmes détectés
- les cartes dupliquées concernées

---

## Statistiques

### Récupérer les statistiques d’un deck

```http
GET /decks/{id}/stats
```

La réponse contient notamment :

- total de cartes
- CMC moyen
- répartition par type
- mana curve
- cartes non trouvées sur Scryfall

---

## Suggestions

### Générer des suggestions

```http
POST /decks/{id}/suggestions
```

Cet endpoint utilise :

- le commandant du deck
- Google Gemini
- Scryfall

pour générer plusieurs catégories de suggestions de cartes.

Les cartes déjà présentes dans le deck sont filtrées avant la réponse.

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

Exemple :

```json
[
  "Sol Ring",
  "Command Tower",
  "Arcane Signet"
]
```

---

# Gestion des erreurs

Le backend utilise un `@RestControllerAdvice` pour centraliser certaines erreurs HTTP.

Principaux statuts :

- **400 Bad Request**
  - validation invalide
  - argument métier invalide
- **404 Not Found**
  - deck ou ressource introuvable
- **409 Conflict**
  - conflit métier, par exemple doublon non autorisé
- **429 Too Many Requests**
  - rate limit
- **502 Bad Gateway**
  - erreur lors d’un appel Gemini
- **500 Internal Server Error**
  - erreur serveur inattendue

---

# Architecture

Organisation générale :

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
- Flyway pour les migrations SQL
- Frontend
- Authentification / utilisateurs
- Amélioration des statistiques
- Amélioration des suggestions Gemini
- Tests automatisés plus complets
