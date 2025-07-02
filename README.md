# Qoyri Senpai - Bot Discord

Un bot Discord avancé intégrant les fonctionnalités d'École Directe pour les étudiants français.

## 🚀 Fonctionnalités

### École Directe
- **Emploi du temps** : Consultation et génération d'images des emplois du temps
- **Devoirs** : Gestion et suivi des devoirs
- **Notes** : Consultation des notes et moyennes
- **Authentification sécurisée** : Chiffrement des identifiants École Directe

### Discord
- **Commandes slash** : Interface moderne avec les commandes Discord
- **Gestion des serveurs** : Configuration personnalisée par serveur
- **Tickets personnalisés** : Système de support
- **Embeds personnalisés** : Messages formatés avec couleurs aléatoires

### Base de données
- **PostgreSQL** : Stockage sécurisé des données
- **Connection pooling** : Optimisation des performances avec HikariCP
- **Chiffrement** : Protection des données sensibles

## 🛠️ Technologies utilisées

- **Java 17** : Langage principal
- **JDA 5.0.0-beta.20** : API Discord pour Java
- **PostgreSQL** : Base de données
- **HikariCP** : Pool de connexions
- **Jackson** : Traitement JSON
- **JSoup** : Parsing HTML
- **BCrypt** : Hachage sécurisé
- **Maven** : Gestion des dépendances

## 📋 Prérequis

- Java 17 ou supérieur
- PostgreSQL
- Un token de bot Discord
- Accès à l'API École Directe

## ⚙️ Configuration

1. Clonez le repository
2. Configurez votre base de données PostgreSQL
3. Créez un fichier `config.json` dans `src/main/java/code/config/`
4. Renseignez les informations nécessaires (token Discord, BDD, etc.)

## 🚀 Installation

```bash
# Cloner le repository
git clone <url-du-repository>

# Naviguer dans le dossier
cd qoyri_senpai

# Compiler avec Maven
mvn clean compile

# Exécuter
mvn exec:java -Dexec.mainClass="code.Main"
```

## 📁 Structure du projet

```
src/main/java/code/
├── Main.java                    # Point d'entrée principal
├── CommandHandler.java          # Gestionnaire des commandes
├── Config.java                  # Configuration du bot
├── base/                        # Classes de base
├── ecole_directe/              # Intégration École Directe
│   ├── authentification.java
│   ├── emploi_du_temps/
│   ├── devoir/
│   └── notes/
├── config/                      # Configuration et BDD
│   └── Database/
├── encryption/                  # Chiffrement des données
└── json/                       # Stockage des données JSON
```

## 🔒 Sécurité

- Chiffrement des identifiants École Directe
- Tokens et mots de passe non exposés dans le code
- Validation des entrées utilisateur
- Connexions sécurisées à la base de données

## 📝 Commandes disponibles

- `/help` : Affiche l'aide
- `/info` : Informations sur le bot
- `/emploi` : Consulter l'emploi du temps
- `/devoirs` : Gérer les devoirs
- `/notes` : Consulter les notes
- `/auth` : S'authentifier à École Directe

## 👥 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une issue ou proposer une pull request.

## 📄 Licence

Ce projet est sous licence [préciser la licence].

## ⚠️ Avertissement

Ce bot utilise l'API non officielle d'École Directe. Utilisez-le de manière responsable et respectez les conditions d'utilisation d'École Directe.
