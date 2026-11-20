package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Réservations", description = "Réservations et établissement de leur contrat")
public class ReservationController {

    private final IReservationService reservationService;
    private final IContratService contratService;

    public ReservationController(IReservationService reservationService, IContratService contratService) {
        this.reservationService = reservationService;
        this.contratService = contratService;
    }

    @GetMapping
    @Operation(summary = "Lister les réservations")
    @ApiResponse(responseCode = "200", description = "Liste des réservations")
    public List<Reservation> findAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lire une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")})
    public Reservation findById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une réservation pour un client et un véhicule donnés")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée"),
            @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable"),
            @ApiResponse(responseCode = "409", description = "Dates incohérentes")})
    public Reservation creerReservation(@RequestParam Long clientId, @RequestParam Long vehiculeId,
                                        @RequestBody Reservation reservation) {
        return reservationService.creerReservation(clientId, vehiculeId, reservation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les attributs d'une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation modifiée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "Dates incohérentes")})
    public Reservation update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return reservationService.update(id, reservation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une réservation (et en cascade son contrat et ses paiements)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")})
    public void delete(@PathVariable Long id) {
        reservationService.delete(id);
    }

    @PostMapping("/{id}/contrat")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Établir le contrat d'une réservation, avec ses paiements, en une seule opération")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat et paiements créés"),
            @ApiResponse(responseCode = "400", description = "Paiement invalide : rollback complet"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "La réservation possède déjà un contrat")})
    public Contrat creerContrat(@PathVariable Long id, @RequestBody Contrat contrat) {
        return contratService.creerContrat(id, contrat);
    }
}
