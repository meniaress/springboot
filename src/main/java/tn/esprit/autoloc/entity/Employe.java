package tn.esprit.autoloc.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.autoloc.enums.RoleEmploye;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private RoleEmploye role;

    /** Côté propriétaire (FK id_agence). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;
}
