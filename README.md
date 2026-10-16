# AutoLoc API — Atelier 2 (Associations JPA : Cascade et Fetch)

Java 17 · Spring Boot 3.3 · Spring Data JPA · Hibernate · MySQL · Lombok

## Lancer
1. Démarrer MySQL (user `root`, mot de passe vide par défaut — voir `application.properties`).
2. **Option A — Hibernate génère le schéma** : `mvn spring-boot:run` (`ddl-auto=update`, la base `autoloc` est créée automatiquement).
3. **Option B — scripts SQL** :
   ```
   mysql -u root -p < sql/01_schema.sql
   mysql -u root -p < sql/02_data.sql
   mysql -u root -p < sql/03_verifications.sql
   ```
   puis lancer l'application.

## Structure
- `entity/` 9 entités · `enums/` 5 énumérations · `repository/` 9 repositories
- `sql/` schéma, données de test, requêtes de vérification
- `docs/fetch-strategy.md` note technique de l'atelier
