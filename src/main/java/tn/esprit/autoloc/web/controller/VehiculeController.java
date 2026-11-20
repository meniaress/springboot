package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IMaintenanceService;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@Tag(name = "Véhicules", description = "CRUD des véhicules, équipements et maintenances")
public class VehiculeController {

    private final IVehiculeService vehiculeService;
    private final IMaintenanceService maintenanceService;

    public VehiculeController(IVehiculeService vehiculeService, IMaintenanceService maintenanceService) {
        this.vehiculeService = vehiculeService;
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    @Operation(summary = "Lister les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules")
    public List<Vehicule> findAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire un véhicule (agence, équipements, maintenances)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")})
    public Vehicule findById(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un véhicule")
    @ApiResponse(responseCode = "201", description = "Véhicule créé")
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les attributs d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")})
    public Vehicule update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un véhicule sans historique")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Véhicule avec réservations ou maintenances")})
    public void delete(@PathVariable Long id) {
        vehiculeService.delete(id);
    }

    @GetMapping("/{id}/equipements")
    @Operation(summary = "Lister les équipements d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipements du véhicule"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")})
    public List<Equipement> findEquipements(@PathVariable Long id) {
        return vehiculeService.findEquipements(id);
    }

    @PostMapping("/{id}/equipements/{equipementId}")
    @Operation(summary = "Affecter un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté (ligne ajoutée dans vehicule_equipement)"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable"),
            @ApiResponse(responseCode = "409", description = "Équipement déjà affecté à ce véhicule")})
    public Vehicule affecterEquipement(@PathVariable Long id, @PathVariable Long equipementId) {
        return vehiculeService.affecterEquipement(id, equipementId);
    }

    @DeleteMapping("/{id}/equipements/{equipementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retirer un équipement d'un véhicule (l'équipement est conservé)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Équipement retiré (ligne supprimée de vehicule_equipement)"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable"),
            @ApiResponse(responseCode = "409", description = "Équipement non affecté à ce véhicule")})
    public void desaffecterEquipement(@PathVariable Long id, @PathVariable Long equipementId) {
        vehiculeService.desaffecterEquipement(id, equipementId);
    }

    @PostMapping("/{id}/maintenances")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Planifier une maintenance pour un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Maintenance planifiée"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Dates incohérentes")})
    public Maintenance planifierMaintenance(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        return maintenanceService.planifierMaintenance(id, maintenance);
    }
}
