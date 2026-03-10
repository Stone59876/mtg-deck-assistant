# MTG Deck Assistant (Spring Boot)

API backend en **Java / Spring Boot** pour gérer des decks **Magic: The Gathering** : création de decks, gestion des cartes (slots), import de decklists (format texte type Archidekt/Moxfield) et endpoints de consultation.

---

## Stack
- **Java 17**
- **Spring Boot** (Spring Web, Validation)
- **Spring Data JPA**
- **H2** (base de données de dev)

---

## Fonctionnalités
- Création / consultation de decks
- Ajout de cartes dans un deck avec **upsert** (merge de quantité si la carte existe déjà)
- Liste des cartes d’un deck
- Import de decklist :
    - **JSON** (`/decks/{id}/import`)
    - **Plain text** (`/decks/{id}/import-text`)
- Résumé d’import : `addedSlots`, `updatedSlots`, `duplicateLines`, `ignoredLines`, `invalidLines`

---

## Lancer le projet

### Prérequis
- Java 17

### Démarrage (Maven)
```bash
./mvnw spring-boot:run
```

## Base de données (H2)

### Console H2 :

```
http://localhost:8080/h2-console
```

#### Valeurs courantes :

JDBC URL : ```jdbc:h2:mem:testdb```

User / Password : selon src/main/resources/application.properties

⚠️ H2 est en mémoire : la base est réinitialisée à chaque redémarrage de l’application.

## API — Exemples (Postman)
### Créer un deck

POST /decks

Body (JSON) :
```
{
"name": "Ezio",
"format": "COMMANDER"
}
```
### Récupérer un deck

GET /decks/{id}

### Ajouter une carte (merge qty)

POST /decks/{id}/cards

Body (JSON) :
```
{
"cardName": "Sol Ring",
"qty": 1
}
```

### Lister les cartes d’un deck

GET /decks/{id}/cards

### Import decklist (JSON)

POST /decks/{id}/import
Body (JSON) :
```
{
"decklist": "1 Sol Ring\n2 Island\n1 Command Tower",
"mergeDuplicates": true
}
```

### Import decklist (Plain text)

POST /decks/{id}/import-text
Header :

Content-Type: text/plain

Body (raw text) :
```
1 Sol Ring
2 Island
1 Command Tower
```

## Gestion d’erreurs (HTTP)

#### 400 : validation des inputs (@Valid) ou IllegalArgumentException

#### 404 : deck introuvable

#### 500 : erreur serveur inattendue

## Roadmap

### Définir un commandant par deck

### Endpoint de validation Commander (100 cartes, singleton, etc.)

### Intégration Scryfall (lookup/search) + cache

### Suggestions via Gemini : génération de requêtes Scryfall + preview de cartes

### UI Next.js


---

### Exemple Postman — Import plain text (body à coller)
```text
1 Sol Ring
2 Island
1 Command Tower
```
### Exemple JSON — Import (body à coller)
```
{
  "decklist": "1 Sol Ring\n2 Island\n1 Command Tower",
  "mergeDuplicates": true
}
```
### Exemple JSON — Add card (body à coller)
```
{
  "cardName": "Sol Ring",
  "qty": 1
}
```