package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit.autoloc.enums.StatutReservation;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    /** Côté propriétaire (FK id_client). Modifié uniquement via Client.addReservation / removeReservation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("reservations")
    private Client client;

    /** Côté propriétaire (FK id_vehicule) - aucune cascade, unidirectionnel. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"maintenances", "equipements", "agence"})
    private Vehicule vehicule;

    /** Côté inverse du OneToOne - cascade ALL : le contrat suit le cycle de vie de la réservation. */
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"reservation", "paiements"})
    private Contrat contrat;

    public void etablirContrat(Contrat contrat) {
        this.contrat = contrat;
        contrat.setReservation(this);
    }

    public void retirerContrat() {
        if (contrat != null) {
            contrat.setReservation(null);
            contrat = null;
        }
    }
}
