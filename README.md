# JWTAuthDemo

Démo d'implémentation d'**authentification JWT** avec **Spring Boot 3**, Spring Security et une API REST stateless.

---

## Table des matières

- [Stack technique](#stack-technique)
- [Fonctionnalités](#fonctionnalités)
- [Structure du projet](#structure-du-projet)
- [Prérequis](#prérequis)
- [Lancer le projet](#lancer-le-projet)
- [Docker Compose](#docker-compose)
- [Configuration et variables d'environnement](#configuration-et-variables-denvironnement)
- [API](#api)
- [Documentation Swagger](#documentation-swagger)
- [Utilisateurs de démo](#utilisateurs-de-démo)
- [Sécurité](#sécurité)

---

## Stack technique

| Technologie           | Version / Détail                       |
| --------------------- | -------------------------------------- |
| **Java**              | 17                                     |
| **Spring Boot**       | 3.5.7                                  |
| **Spring Security**   | (inclus) – sessions stateless, JWT     |
| **Spring Data JPA**   | PostgreSQL                             |
| **JWT**               | jjwt 0.13.0 (génération et validation) |
| **Validation**        | Bean Validation (Jakarta)              |
| **Documentation API** | SpringDoc OpenAPI 2.8.5 (Swagger UI)   |
| **Base de données**   | PostgreSQL 16 (Docker Compose)         |
| **Outils**            | Lombok, Maven                          |

---

## Fonctionnalités

### Authentification

- **Inscription** (`POST /api/v1/auth/register`) : création de compte avec email, mot de passe, prénom et nom ; retour d’un JWT.
- **Connexion** (`POST /api/v1/auth/authenticate`) : authentification par email/mot de passe ; retour d’un JWT.
- **JWT** : token Bearer (HS256), durée de vie configurable ; validation à chaque requête sur les endpoints protégés.

### Profil utilisateur

- **Profil courant** (`GET /api/v1/users/me`) : retourne les infos de l’utilisateur connecté (sans mot de passe). Réservé aux utilisateurs authentifiés.

### Administration (rôle ADMIN)

- **Liste des utilisateurs** (`GET /api/v1/admin/users`) : liste tous les utilisateurs (réservé aux ADMIN).
- **Suppression d’un utilisateur** (`DELETE /api/v1/admin/delete/{id}`) : suppression par id ; un admin ne peut pas se supprimer lui-même.

### Technique

- **Validation des entrées** : `@Valid` sur les DTOs (email valide, champs requis, etc.).
- **Gestion centralisée des erreurs** : `GlobalExceptionHandler` avec réponses JSON homogènes (400, 401, 403, 404, 409, 500).
- **Handlers personnalisés 401/403** : réponses formatées pour token manquant/invalide et accès refusé.
- **Sécurité au niveau méthode** : `@PreAuthorize("hasAuthority('ADMIN')")` sur les endpoints admin.
- **Données initiales** : au premier démarrage, création d’un utilisateur admin (voir [Utilisateurs de démo](#utilisateurs-de-démo)).
- **Documentation OpenAPI** : Swagger UI avec schéma Bearer JWT pour tester les endpoints protégés.

---

## Structure du projet

```
src/main/java/dev/aniss/jwtauthdemo/
├── config/                    # Configuration générale
│   ├── ApplicationConfig.java # UserDetailsService, AuthenticationProvider, PasswordEncoder
│   ├── DataInitializer.java   # Création de l'admin au démarrage
│   ├── OpenApiConfig.java     # Configuration Swagger + schéma Bearer JWT
│   └── security/              # Sécurité
│       ├── SecurityConfig.java
│       ├── JwtAuthtenticationFIlter.java
│       ├── CustomAuthenticationEntryPoint.java
│       └── CustomAccessDeniedHandler.java
├── controllers/
│   ├── AuthenticationController.java  # /api/v1/auth
│   ├── UserController.java           # /api/v1/users (ex. /me)
│   └── AdminController.java          # /api/v1/admin (ADMIN only)
├── dto/                       # Requêtes / réponses API
├── exception/                 # Exceptions métier + GlobalExceptionHandler
├── mapper/                    # Mapper User → UserResponse
├── service/
│   ├── AuthenticationService.java
│   ├── UserService.java
│   └── JwtService.java
└── user/                      # Entité User, Role, UserRepository
```

---

## Prérequis

- **JDK 17**
- **Maven 3.8+**
- **Docker et Docker Compose** (pour PostgreSQL et Adminer, ou une instance PostgreSQL locale)

---

## Lancer le projet

### 1. Démarrer la base de données (Docker Compose)

Se placer à la racine du projet et exécuter :

```bash
docker compose up -d
```

Cela démarre :

- **PostgreSQL** sur `localhost:5432` (base `postgres`, user `postgres`, mot de passe `password`)
- **Adminer** sur `http://localhost:8081` (interface web pour la base)

Pour utiliser une base locale (sans Docker), configurer l’URL, l’utilisateur et le mot de passe dans `application.yml` ou via les variables d’environnement Spring.

### 2. Lancer l’application Spring Boot

Exécuter :

```bash
mvn spring-boot:run
```

Ou exécuter la classe `JwtAuthDemoApplication` depuis l’IDE.

L’API est alors disponible sur **http://localhost:8080**.

### 3. (Optionnel) Désactiver Docker Compose au démarrage

Pour utiliser une base déjà lancée manuellement sans que Spring Boot démarre les conteneurs :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Ajouter dans `application-dev.yml` (ou configurer en ligne de commande) :

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.docker.compose.DockerComposeAutoConfiguration
```

Ou commenter la dépendance `spring-boot-docker-compose` dans le `pom.xml` pour lancer l’application sans le module Docker Compose.

---

## Docker Compose

Le fichier `compose.yaml` à la racine du projet définit deux services utilisés pour le développement et la démo : une base PostgreSQL et l’interface Adminer.

### Services

| Service    | Image              | Rôle                                                                 |
| ---------- | ------------------ | -------------------------------------------------------------------- |
| **postgres** | `postgres:16-alpine` | Base de données PostgreSQL. Exposée sur le port **5432**. Données persistées dans le volume `postgres-data`. |
| **adminer**  | `adminer:4`         | Interface web d’administration de la base. Accessible sur **http://localhost:8081**. Démarre après le healthcheck de PostgreSQL. |

Le service **adminer** dépend de **postgres** avec la condition `service_healthy` : Adminer ne démarre qu’une fois PostgreSQL prêt à accepter des connexions.

### Variables d’environnement Docker

Les variables ci-dessous sont définies dans `compose.yaml` pour chaque service. Pour les surcharger sans modifier le fichier, utiliser un fichier `.env` à la racine du projet ou passer des variables d’environnement au lancement de `docker compose`.

#### Service `postgres`

| Variable             | Description                          | Valeur par défaut |
| -------------------- | ------------------------------------ | ----------------- |
| `POSTGRES_USER`      | Utilisateur PostgreSQL créé au premier démarrage | `postgres` |
| `POSTGRES_PASSWORD`  | Mot de passe de cet utilisateur      | `password`        |
| `POSTGRES_DB`        | Nom de la base de données créée     | `postgres`        |

L’application Spring Boot doit utiliser les mêmes valeurs dans `spring.datasource.url`, `spring.datasource.username` et `spring.datasource.password` (ou via les variables d’environnement Spring) pour se connecter à ce conteneur.

#### Service `adminer`

| Variable                  | Description                                    | Valeur par défaut |
| ------------------------- | ---------------------------------------------- | ----------------- |
| `ADMINER_DEFAULT_SERVER`  | Nom du serveur PostgreSQL (nom du service)     | `postgres`        |

Avec cette valeur, Adminer pré-remplit le champ « Serveur » avec le nom du service Docker `postgres`. Pour se connecter depuis l’interface Adminer, utiliser **Système** : PostgreSQL, **Serveur** : `postgres`, **Utilisateur** : `postgres`, **Mot de passe** : `password`.

### Surcharger les variables Docker

Créer un fichier `.env` à la racine (à côté de `compose.yaml`) :

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=mon_mot_de_passe
POSTGRES_DB=postgres
```

Ou exporter les variables avant d’exécuter `docker compose up -d` :

```bash
export POSTGRES_PASSWORD=mon_mot_de_passe
docker compose up -d
```

En cas de changement de `POSTGRES_USER`, `POSTGRES_PASSWORD` ou `POSTGRES_DB`, adapter également la configuration Spring (`application.yml` ou variables `SPRING_DATASOURCE_*`) pour que l’application puisse se connecter à la base.

---

## Configuration et variables d'environnement

### Fichier `application.yml`

| Propriété                       | Description                              | Valeur par défaut                              |
| ------------------------------- | ---------------------------------------- | ---------------------------------------------- |
| `spring.datasource.url`         | URL JDBC PostgreSQL                      | `jdbc:postgresql://localhost:5432/postgres`    |
| `spring.datasource.username`    | Utilisateur BDD                          | `postgres`                                     |
| `spring.datasource.password`    | Mot de passe BDD                         | `password`                                     |
| `spring.jpa.hibernate.ddl-auto` | Création du schéma                       | `create-drop` (recréé à chaque redémarrage)    |
| `jwt.secret-key`                | Clé secrète pour signer les JWT (Base64) | Valeur par défaut en dur (à remplacer en prod) |
| `jwt.expiration-ms`             | Durée de vie du token (ms)               | `1440000` (24 min)                             |

### Variables d'environnement recommandées

Surtout utiles en **production** ou pour surcharger la config sans modifier les fichiers.

| Variable                     | Rôle                                     | Exemple                                  |
| ---------------------------- | ---------------------------------------- | ---------------------------------------- |
| `JWT_SECRET_KEY`             | Clé JWT (Base64, ≥ 32 octets pour HS256) | Générer avec : `openssl rand -base64 32` |
| `JWT_EXPIRATION_MS`          | Durée de vie du token en millisecondes   | `3600000` (1 h)                          |
| `SPRING_DATASOURCE_URL`      | URL JDBC                                 | `jdbc:postgresql://host:5432/dbname`     |
| `SPRING_DATASOURCE_USERNAME` | User BDD                                 | `postgres`                               |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe BDD                         | (secret)                                 |

Exemple de lancement avec variables d’environnement :

```bash
export JWT_SECRET_KEY="$(openssl rand -base64 32)"
export JWT_EXPIRATION_MS=3600000
mvn spring-boot:run
```

---

## API

Base URL : **http://localhost:8080**

### Endpoints publics (sans token)

| Méthode | URL                         | Description |
| ------- | --------------------------- | ----------- |
| `POST`  | `/api/v1/auth/register`     | Inscription |
| `POST`  | `/api/v1/auth/authenticate` | Connexion   |

### Endpoints protégés (header `Authorization: Bearer <token>`)

| Méthode  | URL                         | Rôle        | Description                         |
| -------- | --------------------------- | ----------- | ----------------------------------- |
| `GET`    | `/api/v1/users/me`          | Authentifié | Profil de l’utilisateur connecté    |
| `GET`    | `/api/v1/admin/users`       | ADMIN       | Liste des utilisateurs              |
| `DELETE` | `/api/v1/admin/delete/{id}` | ADMIN       | Suppression d’un utilisateur par id |

### Exemples de corps de requête

**Inscription – `POST /api/v1/auth/register`**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Connexion – `POST /api/v1/auth/authenticate`**

```json
{
  "email": "john@example.com",
  "password": "secret123"
}
```

**Réponse typique (register / authenticate)**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Profil – `GET /api/v1/users/me` (réponse)**

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

---

## Documentation Swagger

- **Swagger UI** : http://localhost:8080/swagger-ui.html (ou `/swagger-ui/index.html`)
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

Pour tester les endpoints protégés :

1. Cliquer sur **Authorize**.
2. Saisir le JWT (sans le préfixe `Bearer `).
3. Envoyer les requêtes ; le token est ajouté automatiquement dans le header.

Les endpoints `/api/v1/auth/*` sont sans cadenas ; les autres (ex. `/me`, admin) exigent le token.

---

## Utilisateurs de démo

Au premier démarrage (si la base est vide), un utilisateur **admin** est créé :

| Email             | Mot de passe | Rôle  |
| ----------------- | ------------ | ----- |
| `admin@admin.com` | `admin`      | ADMIN |

S’authentifier avec ces identifiants pour obtenir un JWT et tester les endpoints admin.

---

## Sécurité

- **Sessions** : stateless (pas de session serveur) ; l’état d’authentification est porté par le JWT.
- **CSRF** : désactivé (API stateless sans cookies de session).
- **Filtre JWT** : lit le header `Authorization: Bearer <token>`, valide le token et remplit le `SecurityContext`.
- **Rôles** : `USER` (par défaut à l’inscription) et `ADMIN` ; les routes `/api/v1/admin/*` sont protégées par `@PreAuthorize("hasAuthority('ADMIN')")`.
- **Mots de passe** : hashés avec BCrypt avant stockage.
- **Erreurs 401/403** : formatées en JSON via des handlers personnalisés (pas de page HTML Spring Security).

---


Le projet contient au minimum un test de chargement du contexte (`@SpringBootTest`). Ajouter des tests unitaires (services, JwtService) et d’intégration (MockMvc ou TestRestTemplate) pour les endpoints d’auth et protégés selon les besoins.

---
