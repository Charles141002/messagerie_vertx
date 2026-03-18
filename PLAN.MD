# Plan de développement : Application de Messagerie Instantanée Vert.x

Ce document détaille les étapes pour réaliser le TP de messagerie instantanée en utilisant Vert.x, une base de données SQL (HSQLDB) et SockJS pour le temps réel.

## 🏗️ Architecture Technique

L'application sera découpée en plusieurs **Verticles** communiquant via l'**Event Bus** :
1.  **MainVerticle** : Point d'entrée, déploie les autres verticles.
2.  **DatabaseVerticle** : Gère les accès SQL (implémenté via un service proxy).
3.  **HttpServerVerticle** : Gère l'API REST, le serveur de fichiers statiques (IHM) et le pont SockJS (Websocket).

---

## 📅 Étapes de réalisation

### 1. Initialisation du projet 🛠️
- Ajouter la dépendance `hsqldb` dans le `pom.xml` pour la base de données.
- Configurer les dossiers : `src/main/resources/webroot` pour l'IHM.

### 2. Couche de Persistance (Base de Données) 🗄️
- Créer une interface `MessagingService` annotée avec `@ProxyGen`.
- Implémenter `MessagingServiceImpl` :
    - Initialiser HSQLDB et créer la table `messages` (id, user_name, content, timestamp).
    - Méthode `getLastMessages(Handler<AsyncResult<List<JsonObject>>> resultHandler)`.
    - Méthode `addMessage(JsonObject message, Handler<AsyncResult<Void>> resultHandler)`.
- Créer le `DatabaseVerticle` pour enregistrer ce service sur l'Event Bus.

### 3. API Webservice (Backend) 🌐
- Configurer `HttpServerVerticle` avec un `Router` Vert.x Web.
- **Routes REST** :
    - `GET /api/messages` : Appelle le service de base de données.
    - `POST /api/messages` : Reçoit un message, l'enregistre via le service, et publie une notification sur l'Event Bus.
- **Statique** : Servir `index.html` depuis `webroot`.

### 4. Temps Réel avec SockJS ⚡
- Configurer un `SockJSHandler` dans le `HttpServerVerticle`.
- Mettre en place un **Bridge** (pont) entre l'Event Bus côté serveur et le navigateur.
- Autoriser l'adresse `chat.updates` pour que les clients reçoivent les nouveaux messages.

### 5. Interface Homme-Machine (IHM) 🎨
- Créer `src/main/resources/webroot/index.html`.
- **UI** :
    - Liste des messages (scrollable).
    - Formulaire : Input "Pseudo", Input "Message", Bouton "Envoyer".
- **JS** :
    - Utiliser `fetch()` pour charger les 20 derniers messages au chargement.
    - Utiliser `vertx-eventbus.js` (client SockJS) pour s'abonner aux nouveaux messages en temps réel.
    - Envoyer les messages via un `POST /api/messages`.

### 6. Tests Unitaires 🧪
- Utiliser `VertxTestContext` et JUnit 5.
- Tester l'insertion et la récupération de messages dans la base de données.
- Tester l'API HTTP.

### 7. Bonus (Optionnel) ✨
- Ajouter `GET /api/message/:id`.
- Ajouter `PUT /api/messages` (modification).
- Ajouter `DELETE /api/message/:id` (suppression).
- Mettre à jour l'IHM pour refléter ces changements en temps réel via l'Event Bus.

---

## 🚀 Commande pour lancer l'application
```bash
mvn compile exec:java
```
