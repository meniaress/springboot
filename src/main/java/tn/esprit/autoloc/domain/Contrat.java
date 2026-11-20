package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    private LocalDate dateSignature;
    private BigDecimal montantTotal;
    private boolean valide;

    /** Côté propriétaire du OneToOne (FK id_reservation, unique). Modifié via Reservation.etablirContrat. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"contrat", "client", "vehicule"})
    private Reservation reservation;

    /** Composition : cascade ALL + orphanRemoval, LAZY. */
    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("contrat")
    private List<Paiement> paiements = new ArrayList<>();

    /** Utilisé par Jackson à la désérialisation : chaque paiement reçu est rattaché au contrat. */
    public void setPaiements(List<Paiement> paiements) {
        this.paiements.clear();
        if (paiements != null) {
            paiements.forEach(this::addPaiement);
        }
    }

    public void addPaiement(Paiement paiement) {
        paiements.add(paiement);
        paiement.setContrat(this);
    }

    public void removePaiement(Paiement paiement) {
        paiements.remove(paiement);
        paiement.setContrat(null);
    }
}
