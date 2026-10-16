package tn.esprit.autoloc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;

    private String libelle;

    /** Côté inverse - aucune cascade : équipements partagés entre véhicules. */
    @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY)
    private Set<Vehicule> vehicules = new HashSet<>();
}
