package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@Tag(name = "Contrats", description = "Contrats et gestion de leurs paiements (composition)")
public class ContratController {

    private final IContratService contratService;

    public ContratController(IContratService contratService) {
        this.contratService = contratService;
    }

    @GetMapping
    @Operation(summary = "Lister les contrats (avec paiements)")
    @ApiResponse(responseCode = "200", description = "Liste des contrats")
    public List<Contrat> findAll() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public Contrat findById(@PathVariable Long id) {
        return contratService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les attributs d'un contrat (hors paiements)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public Contrat update(@PathVariable Long id, @RequestBody Contrat contrat) {
        return contratService.update(id, contrat);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un contrat et, en cascade, ses paiements")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paiements puis contrat supprimés"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public void delete(@PathVariable Long id) {
        contratService.delete(id);
    }

    @PostMapping("/{id}/paiements")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter un paiement à un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement ajouté"),
            @ApiResponse(responseCode = "400", description = "Paiement invalide"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")})
    public Paiement ajouterPaiement(@PathVariable Long id, @RequestBody Paiement paiement) {
        return contratService.ajouterPaiement(id, paiement);
    }

    @DeleteMapping("/{id}/paiements/{paiementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retirer un paiement du contrat (supprimé en base par orphanRemoval)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paiement supprimé"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable"),
            @ApiResponse(responseCode = "409", description = "Le paiement appartient à un autre contrat")})
    public void retirerPaiement(@PathVariable Long id, @PathVariable Long paiementId) {
        contratService.retirerPaiement(id, paiementId);
    }
}
