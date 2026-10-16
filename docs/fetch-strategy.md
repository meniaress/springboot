# Note technique — Stratégie de fetch et de cascade (AutoLoc)

| Association | Fetch | Cascade | Justification |
|---|---|---|---|
| Contrat → Paiement | LAZY | ALL + orphanRemoval | Un paiement n'existe que rattaché à son contrat (composition) ; supprimer le contrat supprime ses paiements, et retirer un paiement de la liste le supprime en base. |
| Agence → Vehicule | LAZY | Aucune | Un véhicule survit à la suppression de son agence ; une cascade REMOVE détruirait le parc et l'historique. La collection n'est chargée qu'à la demande. |
| Agence → Employe | LAZY | Aucune | Un employé a une existence propre (réaffectation possible) ; supprimer une agence ne doit pas supprimer les employés. |
| Vehicule ↔ Equipement | LAZY | Aucune (`Set`) | Les équipements sont partagés entre véhicules : supprimer un véhicule ne doit pas supprimer un équipement utilisé ailleurs. `Set` pour éviter les doublons et les problèmes de bag Hibernate. |
| Client → Reservation | LAZY | PERSIST | Enregistrer un client avec de nouvelles réservations les enregistre aussi, mais l'historique des réservations ne doit pas disparaître avec le client (pas de REMOVE). |
| Reservation → Vehicule | LAZY | Aucune | Un véhicule existe indépendamment de ses réservations ; `@ManyToOne` forcé en LAZY (EAGER par défaut) pour ne pas charger toute la chaîne d'objets liés. |
| Reservation ↔ Contrat | LAZY | ALL (côté Reservation) | Le contrat est issu de la réservation et n'a pas de sens sans elle : son cycle de vie suit celui de la réservation. Remarque : côté inverse (`mappedBy`), un `@OneToOne` ne peut pas être réellement LAZY sans instrumentation bytecode ; côté propriétaire (Contrat) le LAZY fonctionne. |
| Vehicule → Maintenance | LAZY | PERSIST | Une maintenance est créée avec son véhicule, mais l'historique de maintenance est conservé (pas de REMOVE). |

## Choix transverses

- **Côté propriétaire** = côté `@ManyToOne` (porte la clé étrangère) ; côté inverse = `mappedBy`, aucune colonne créée.
- **Toutes les associations en LAZY** : une lecture ne charge que ce dont elle a besoin. Un accès hors transaction lève une `LazyInitializationException` à traiter par une requête dédiée (Atelier 7). `spring.jpa.open-in-view=false` est activé volontairement.
- **Lombok ciblé** (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`), jamais `@Data` : `toString`/`hashCode` parcourraient les deux côtés des associations bidirectionnelles (risque de `StackOverflowError`).
- **Méthodes utilitaires** (`addPaiement`, `addMaintenance`, `addEquipement`…) pour garder les deux côtés synchronisés.

## Clés étrangères générées

| Table | Colonne FK | Cible |
|---|---|---|
| paiement | contrat_id_contrat (nom par défaut Hibernate) | contrat |
| vehicule | id_agence | agence |
| employe | id_agence | agence |
| maintenance | id_vehicule | vehicule |
| reservation | id_client | client |
| reservation | id_vehicule | vehicule |
| contrat | id_reservation (unique) | reservation |
| vehicule_equipement | id_vehicule, id_equipement | vehicule, equipement |
