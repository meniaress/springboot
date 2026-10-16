package tn.esprit.autoloc.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.autoloc.enums.CategorieVehicule;
import tn.esprit.autoloc.enums.StatutVehicule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;
    private String marque;
    private String modele;

    @Enumerated(EnumType.STRING)
    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    /** Côté propriétaire (FK id_agence) - aucune cascade. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    private Agence agence;

    /** Côté inverse - PERSIST : une maintenance est enregistrée avec son véhicule. */
    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Maintenance> maintenances = new ArrayList<>();

    /** Côté propriétaire ManyToMany (table vehicule_equipement) - Set, aucune cascade. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement"))
    private Set<Equipement> equipements = new HashSet<>();

    public void addMaintenance(Maintenance maintenance) {
        maintenances.add(maintenance);
        maintenance.setVehicule(this);
    }

    public void addEquipement(Equipement equipement) {
        equipements.add(equipement);
        equipement.getVehicules().add(this);
    }

    public void removeEquipement(Equipement equipement) {
        equipements.remove(equipement);
        equipement.getVehicules().remove(this);
    }
}
