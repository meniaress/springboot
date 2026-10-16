package tn.esprit.autoloc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;
    private String ville;
    private String adresse;
    private String telephone;

    /** Côté inverse - aucune cascade : un véhicule survit à son agence. */
    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    private List<Vehicule> vehicules = new ArrayList<>();

    /** Côté inverse - aucune cascade. */
    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    private List<Employe> employes = new ArrayList<>();
}
