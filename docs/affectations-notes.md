# Atelier 8 — Affectations, désaffectations et cascade

## 1. Relations traitées

| Relation | Type | Côté propriétaire (FK) | mappedBy | Cascade | orphanRemoval | Chargement | Méthodes de synchronisation |
|---|---|---|---|---|---|---|---|
| Agence – Employe | OneToMany / ManyToOne | `Employe.agence` (`employe.id_agence`) | `Agence.employes` → `"agence"` | Aucune | Non | LAZY | `Agence.addEmploye` / `removeEmploye` |
| Agence – Vehicule | OneToMany / ManyToOne | `Vehicule.agence` (`vehicule.id_agence`) | `Agence.vehicules` → `"agence"` | Aucune | Non | LAZY | `Agence.addVehicule` / `removeVehicule` |
| Vehicule – Equipement | ManyToMany (`Set`) | `Vehicule.equipements` (table `vehicule_equipement`) | `Equipement.vehicules` → `"equipements"` | Aucune | — | LAZY | `Vehicule.addEquipement` / `removeEquipement` |
| Reservation – Contrat | OneToOne | `Contrat.reservation` (`contrat.id_reservation`, UNIQUE) | `Reservation.contrat` → `"reservation"` | ALL (côté Reservation) | Non | LAZY | `Reservation.etablirContrat` / `retirerContrat` |
| Contrat – Paiement | OneToMany (composition) | `Paiement.contrat` (`paiement.contrat_id_contrat`) | `Contrat.paiements` → `"contrat"` | ALL | **Oui** | LAZY | `Contrat.addPaiement` / `removePaiement` (+ `setPaiements` pour Jackson) |
| Client – Reservation | OneToMany / ManyToOne | `Reservation.client` (`reservation.id_client`) | `Client.reservations` → `"client"` | PERSIST | Non | LAZY | `Client.addReservation` / `removeReservation` |
| Reservation → Vehicule | ManyToOne (unidirectionnel) | `Reservation.vehicule` (`reservation.id_vehicule`) | — | Aucune | — | LAZY | — |
| Vehicule – Maintenance | OneToMany / ManyToOne | `Maintenance.vehicule` (`maintenance.id_vehicule`) | `Vehicule.maintenances` → `"vehicule"` | PERSIST | Non | LAZY | `Vehicule.addMaintenance` / `removeMaintenance` |

**Sérialisation JSON** : chaque association porte `@JsonIgnoreProperties` (côté opposé ignoré → pas de boucle infinie, le lien reste visible des deux côtés) et `@JsonProperty(access = READ_ONLY)` (un lien ne peut pas être modifié par le corps d'un POST/PUT ; seuls les endpoints d'affectation le modifient). `Contrat.paiements` reste en écriture pour créer un contrat avec ses paiements. Le module `Hibernate6Module` sérialise un proxy LAZY non chargé par son seul identifiant (`open-in-view=false`).

## 2. Endpoints

| Méthode | URI | Effet | Codes |
|---|---|---|---|
| POST | `/api/agences/{agenceId}/employes/{employeId}` | Affecte un employé | 200 / 404 / 409 |
| DELETE | `/api/agences/{agenceId}/employes/{employeId}` | Désaffecte l'employé | 204 / 404 / 409 |
| POST | `/api/agences/{agenceId}/vehicules/{vehiculeId}` | Affecte un véhicule | 200 / 404 / 409 |
| DELETE | `/api/agences/{agenceId}/vehicules/{vehiculeId}` | Désaffecte le véhicule | 204 / 404 / 409 |
| PUT | `/api/agences/{agenceCibleId}/vehicules/{vehiculeId}` | Transfère le véhicule (atomique) | 200 / 404 / 409 |
| POST | `/api/vehicules/{id}/equipements/{equipementId}` | Affecte un équipement | 200 / 404 / 409 |
| DELETE | `/api/vehicules/{id}/equipements/{equipementId}` | Retire l'équipement | 204 / 404 / 409 |
| POST | `/api/vehicules/{id}/maintenances` | Planifie une maintenance | 201 / 404 / 409 |
| POST | `/api/reservations?clientId=&vehiculeId=` | Crée une réservation | 201 / 404 / 409 |
| POST | `/api/reservations/{id}/contrat` | Crée le contrat avec ses paiements | 201 / 400 / 404 / 409 |
| POST | `/api/contrats/{id}/paiements` | Ajoute un paiement | 201 / 400 / 404 |
| DELETE | `/api/contrats/{id}/paiements/{paiementId}` | Retire (supprime) le paiement | 204 / 404 / 409 |
| DELETE | `/api/contrats/{id}` | Supprime le contrat et ses paiements | 204 / 404 |

Codes : `ResourceNotFoundException` → **404**, `BusinessRuleException` (règle de gestion) → **409**, violation Bean Validation → **400** (`GlobalExceptionHandler`, réponses `ProblemDetail`).
Swagger UI : <http://localhost:8081/swagger-ui/index.html>.

## 3. Résultats des scénarios

| N° | Scénario | Requête | HTTP obtenu | Résultat | Capture |
|---|---|---|---|---|---|
| 1 | Affecter un employé puis lire l'agence | `POST /agences/1/employes/1`, `GET /agences/1`, `GET /employes/1` | 200, 200, 200 | L'employé apparaît dans `agence.employes` et `employe.agence` | `swagger/s01.png` |
| 2 | Affecter deux fois le même employé | `POST /agences/1/employes/1` | **409** | « L'employé 1 est déjà affecté à l'agence 1 », aucune requête SQL d'écriture | `swagger/s02.png` |
| 3 | Désaffecter puis lire l'employé | `DELETE /agences/1/employes/1`, `GET /employes/1` | 204, 200 | Employé conservé, `agence: null` | `swagger/s03.png` |
| 4 | Affecter puis retirer un équipement | `POST` puis `DELETE /vehicules/1/equipements/1`, `GET /equipements/1` | 200, 204, 200 | Ligne de jointure ajoutée puis supprimée ; équipement conservé | `swagger/s04.png` |
| 5 | Contrat + 3 paiements en un appel | `POST /reservations/1/contrat` | 201 | 1 INSERT contrat puis 3 INSERT paiement | `swagger/s05.png` |
| 6 | Retirer un paiement | `DELETE /contrats/1/paiements/2`, `GET /paiements/2` | 204, 404 | Paiement supprimé en base (orphanRemoval) | `swagger/s06.png` |
| 7 | Second contrat pour la même réservation | `POST /reservations/1/contrat` | **409** | « La réservation 1 possède déjà un contrat (id = 1) » | `swagger/s07.png` |
| 8 | Supprimer un contrat | `DELETE /contrats/1`, `GET /paiements` | 204, 200 (`[]`) | DELETE des paiements puis du contrat | `swagger/s08.png` |
| 9 | Paiement invalide (montant −5) parmi 3 | `POST /reservations/2/contrat` | **400** | Rollback complet : `GET /contrats` et `GET /paiements` renvoient `[]` | `swagger/s09.png` |
| 10 | Identifiant inexistant | `GET /agences/999`, `POST /agences/1/employes/999` | **404** | « Agence introuvable (id = 999) » | `swagger/s10.png` |

> Codes relevés en rejouant les scénarios par appels HTTP sur une base vierge. Les captures Swagger UI sont à déposer dans `docs/swagger/` sous les noms indiqués.

## 4. Requêtes SQL observées

**Scénario 4 — ManyToMany**
```sql
insert into vehicule_equipement (id_vehicule,id_equipement) values (?,?)   -- affectation
delete from vehicule_equipement where id_vehicule=?                        -- retrait
```
Seule la table de jointure est modifiée : aucune requête sur `equipement`, qui est conservé. Pour un `Set`, Hibernate supprime les lignes du véhicule puis réinsère celles qui restent (aucune ici).

**Scénario 5 — création en cascade**
```sql
insert into contrat (date_signature,montant_total,id_reservation,valide) values (?,?,?,?)
insert into paiement (contrat_id_contrat,date_paiement,mode_paiement,montant) values (?,?,?,?)
insert into paiement (contrat_id_contrat,date_paiement,mode_paiement,montant) values (?,?,?,?)
insert into paiement (contrat_id_contrat,date_paiement,mode_paiement,montant) values (?,?,?,?)
```
Un seul `save(contrat)` : `CascadeType.PERSIST` (inclus dans ALL) propage l'insertion aux paiements, après le contrat dont l'id (IDENTITY) sert de FK.

**Scénario 6 — orphanRemoval**
```sql
delete from paiement where id_paiement=?
```
Le service appelle seulement `contrat.removePaiement(p)`. Au commit, Hibernate détecte que le paiement a quitté la collection et le supprime, sans appel explicite à un repository.

**Scénario 9 — rollback**
```sql
insert into contrat (...) values (...)
insert into paiement (...) values (...)   -- paiement 1 (100)
insert into paiement (...) values (...)   -- paiement 2 (100)
-- paiement 3 (montant -5) : ConstraintViolationException levée par Hibernate Validator avant l'INSERT
-- => rollback de la transaction : les 3 INSERT déjà exécutés sont annulés
```
Les INSERT partent bien vers MySQL (stratégie IDENTITY), mais la transaction n'est pas validée : l'exception non contrôlée provoque le rollback, et aucune ligne ne subsiste.

## 5. Questions

1. **Pourquoi synchroniser les deux côtés ?** Seul le côté propriétaire est écrit en base. Si on ne met à jour que le côté inverse, rien n'est persisté. Si on ne met à jour que le côté propriétaire, la collection en mémoire reste fausse jusqu'au rechargement : réponse JSON incohérente, cascade et orphanRemoval appliqués sur un état erroné. Les méthodes `add…/remove…` gardent le graphe d'objets et la base cohérents dans la même transaction.
2. **Désaffecter ou supprimer ?** Désaffecter supprime le lien (FK mise à `NULL` ou ligne de jointure supprimée) : l'entité continue d'exister (employé, équipement, véhicule). Supprimer détruit la ligne de l'entité. Dans une composition (Contrat–Paiement), les deux se confondent : un paiement retiré devient orphelin et est supprimé (`orphanRemoval`), et supprimer le contrat supprime ses paiements (`CascadeType.REMOVE`).
3. **Que garantit `@Transactional` ?** L'atomicité : toutes les lectures et écritures de la méthode se font dans une seule transaction, validée au commit ou entièrement annulée sur `RuntimeException` (scénario 9, transfert vers une agence inexistante). Il garantit aussi un contexte de persistance ouvert : dirty checking sans `save`, chargement des collections LAZY. En `readOnly = true`, aucun flush n'est déclenché.
4. **Propriétaire du ManyToMany ?** `Vehicule.equipements`, porteur de `@JoinTable`. `Equipement.vehicules` est le côté inverse (`mappedBy = "equipements"`). Il en résulte la table `vehicule_equipement (id_vehicule, id_equipement)`, clé primaire composée et deux FK. Seules les modifications de `Vehicule.equipements` sont écrites en base.

## 6. Anomalies SonarQube for IDE

> Lancer l'analyse SonarQube for IDE dans l'IDE et reporter ici les anomalies réellement signalées. Points déjà corrigés lors de la reprise du code :

| Problème | Correction |
|---|---|
| Champ `Contrat contrat` erroné dans `Contrat` (`@OneToOne(mappedBy = "reservation")` pointant sur lui-même), qui empêchait le démarrage d'Hibernate | Supprimé |
| Classe `vehiculeServices` vide, sans `@Service` : `VehiculeController` ne pouvait pas être injecté | Remplacée par `VehiculeServiceImpl` |
| `IVehiculeRespository` (faute de frappe) en doublon de `VehiculeRepository` | Supprimé, renommage en `I…Repository` |
| Packages `Services` / `Impls` et nom de classe en minuscule (convention de nommage Java) | `service` / `service.impl`, `I…Service` / `…ServiceImpl` |
| Constantes de chaîne utilisées une seule fois | Supprimées |
| Liens modifiables depuis le corps JSON (règle java:S4684 : entités persistantes en paramètre de `@RequestMapping`) | Associations en `@JsonProperty(READ_ONLY)`, mise à jour limitée aux attributs simples. S4684 peut rester signalée tant que les contrôleurs reçoivent des entités : la correction complète passe par des DTO |
