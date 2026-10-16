package tn.esprit.autoloc.entity;

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

    /** Côté propriétaire (FK id_client). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    /** Côté propriétaire (FK id_vehicule) - aucune cascade. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    /** Côté inverse du OneToOne - cascade ALL : le contrat suit le cycle de vie de la réservation. */
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contrat contrat;

    public void setContratAssocie(Contrat contrat) {
        this.contrat = contrat;
        if (contrat != null) {
            contrat.setReservation(this);
        }
    }
}
