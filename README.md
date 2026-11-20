# AutoLoc API — Atelier 8 (Affectations, désaffectations et cascade)

Java 17 · Spring Boot 3.3 · Spring Data JPA · Hibernate · MySQL · Lombok · SpringDoc (Swagger UI : http://localhost:8081/swagger-ui/index.html)

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
- `domain/` 9 entités (méthodes de synchronisation) · `enums/` · `repository/` 9 `I…Repository`
- `service/` interfaces `I…Service`, `service/impl/` implémentations transactionnelles · `web/controller/` contrôleurs REST · `web/handler/` gestion des erreurs (404 / 409 / 400)
- `sql/` schéma, données de test, requêtes de vérification
- `docs/fetch-strategy.md` (Atelier 2), `docs/affectations-notes.md` (Atelier 8), `docs/swagger/` captures
