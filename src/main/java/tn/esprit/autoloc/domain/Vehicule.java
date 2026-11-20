package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    /** Côté propriétaire (FK id_agence) - aucune cascade. Modifié uniquement via Agence.addVehicule / removeVehicule. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"vehicules", "employes"})
    private Agence agence;

    /** Côté inverse - PERSIST : une maintenance est enregistrée avec son véhicule. */
    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("vehicule")
    private List<Maintenance> maintenances = new ArrayList<>();

    /** Côté propriétaire ManyToMany (table vehicule_equipement) - Set, aucune cascade. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement"))
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Equipement> equipements = new HashSet<>();

    public void addMaintenance(Maintenance maintenance) {
        maintenances.add(maintenance);
        maintenance.setVehicule(this);
    }

    public void removeMaintenance(Maintenance maintenance) {
        maintenances.remove(maintenance);
        maintenance.setVehicule(null);
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
