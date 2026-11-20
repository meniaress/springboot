package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.List;

@RestController
@RequestMapping("/api/agences")
@Tag(name = "Agences", description = "CRUD des agences, affectation des employés et des véhicules")
public class AgenceController {

    private final IAgenceService agenceService;

    public AgenceController(IAgenceService agenceService) {
        this.agenceService = agenceService;
    }

    @GetMapping
    @Operation(summary = "Lister les agences (avec employés et véhicules)")
    @ApiResponse(responseCode = "200", description = "Liste des agences")
    public List<Agence> findAll() {
        return agenceService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")})
    public Agence findById(@PathVariable Long id) {
        return agenceService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une agence")
    @ApiResponse(responseCode = "201", description = "Agence créée")
    public Agence create(@RequestBody Agence agence) {
        return agenceService.create(agence);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les attributs d'une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence modifiée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")})
    public Agence update(@PathVariable Long id, @RequestBody Agence agence) {
        return agenceService.update(id, agence);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une agence (ses employés et véhicules sont désaffectés, pas supprimés)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agence supprimée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")})
    public void delete(@PathVariable Long id) {
        agenceService.delete(id);
    }

    @PostMapping("/{agenceId}/employes/{employeId}")
    @Operation(summary = "Affecter un employé à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé affecté, agence mise à jour renvoyée"),
            @ApiResponse(responseCode = "404", description = "Agence ou employé introuvable"),
            @ApiResponse(responseCode = "409", description = "Employé déjà affecté")})
    public Agence affecterEmploye(@PathVariable Long agenceId, @PathVariable Long employeId) {
        return agenceService.affecterEmploye(agenceId, employeId);
    }

    @DeleteMapping("/{agenceId}/employes/{employeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désaffecter un employé de son agence (l'employé est conservé)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé désaffecté"),
            @ApiResponse(responseCode = "404", description = "Agence ou employé introuvable"),
            @ApiResponse(responseCode = "409", description = "L'employé n'est pas affecté à cette agence")})
    public void desaffecterEmploye(@PathVariable Long agenceId, @PathVariable Long employeId) {
        agenceService.desaffecterEmploye(agenceId, employeId);
    }

    @PostMapping("/{agenceId}/vehicules/{vehiculeId}")
    @Operation(summary = "Affecter un véhicule à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté, agence mise à jour renvoyée"),
            @ApiResponse(responseCode = "404", description = "Agence ou véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Véhicule déjà affecté")})
    public Agence affecterVehicule(@PathVariable Long agenceId, @PathVariable Long vehiculeId) {
        return agenceService.affecterVehicule(agenceId, vehiculeId);
    }

    @DeleteMapping("/{agenceId}/vehicules/{vehiculeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Désaffecter un véhicule de son agence (le véhicule est conservé)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule désaffecté"),
            @ApiResponse(responseCode = "404", description = "Agence ou véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Le véhicule n'est pas affecté à cette agence")})
    public void desaffecterVehicule(@PathVariable Long agenceId, @PathVariable Long vehiculeId) {
        agenceService.desaffecterVehicule(agenceId, vehiculeId);
    }

    @PutMapping("/{agenceCibleId}/vehicules/{vehiculeId}")
    @Operation(summary = "Transférer un véhicule vers une autre agence (opération atomique)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule transféré"),
            @ApiResponse(responseCode = "404", description = "Agence cible ou véhicule introuvable : rollback"),
            @ApiResponse(responseCode = "409", description = "Véhicule sans agence ou déjà dans l'agence cible")})
    public Vehicule transfererVehicule(@PathVariable Long agenceCibleId, @PathVariable Long vehiculeId) {
        return agenceService.transfererVehicule(vehiculeId, agenceCibleId);
    }
}
