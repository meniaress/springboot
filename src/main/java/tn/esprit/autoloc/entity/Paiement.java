package tn.esprit.autoloc.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.autoloc.enums.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;

    private BigDecimal montant;
    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    /** Côté propriétaire : colonne générée par défaut = contrat_id_contrat. */
    @ManyToOne(fetch = FetchType.LAZY)
    private Contrat contrat;
}
