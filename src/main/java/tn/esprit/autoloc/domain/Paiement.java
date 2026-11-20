package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    /** Validé par Hibernate Validator au persist : un montant invalide annule toute la transaction. */
    @NotNull
    @Positive
    private BigDecimal montant;

    private LocalDate datePaiement;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    /** Côté propriétaire : colonne générée par défaut = contrat_id_contrat. Modifié uniquement via Contrat. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"paiements", "reservation"})
    private Contrat contrat;
}
