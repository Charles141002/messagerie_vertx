# Messagerie Vert.x (Partie Obligatoire)

Application de messagerie instantanée simple développée avec **Vert.x 4** et **HSQLDB**.

## Fonctionnalités (Obligatoires)
- **Persistance** : Les messages sont sauvegardés dans une base de données HSQLDB.
- **Consultation** : Récupération des 20 derniers messages à l'ouverture.
- **Temps Réel** : Mise à jour automatique des messages entre les onglets (via polling 2s).

## Configuration
- **Java 21**
- **Maven**
- **Vert.x 4.5.25**

## Lancement

1. Accédez au répertoire du projet :
   ```bash
   cd messagerie-vertx
   ```

2. Compilez et lancez l'application :
   ```bash
   mvn clean compile exec:java
   ```

3. Ouvrez votre navigateur :
   [http://localhost:8888](http://localhost:8888)

## Architecture
- `DatabaseVerticle` : Gère l'initialisation de la base de données.
- `HttpServerVerticle` : Gère les routes API REST et sert les fichiers statiques.
- `MessagingService` : Service asynchrone pour les opérations de données.
