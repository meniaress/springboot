package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementRepository equipementRepository;
    private final IReservationRepository reservationRepository;

    public VehiculeServiceImpl(IVehiculeRepository vehiculeRepository,
                               IEquipementRepository equipementRepository,
                               IReservationRepository reservationRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.equipementRepository = equipementRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findAll() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        vehicules.forEach(VehiculeServiceImpl::chargerAssociations);
        return vehicules;
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule findById(Long id) {
        return chargerAssociations(getVehicule(id));
    }

    @Override
    @Transactional
    public Vehicule create(Vehicule vehicule) {
        vehicule.setIdVehicule(null);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = getVehicule(id);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());
        return chargerAssociations(existant);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicule vehicule = getVehicule(id);
        if (reservationRepository.existsByVehiculeIdVehicule(id) || !vehicule.getMaintenances().isEmpty()) {
            throw new BusinessRuleException(
                    "Le véhicule " + id + " possède un historique (réservations / maintenances) : suppression refusée");
        }
        if (vehicule.getAgence() != null) {
            vehicule.getAgence().removeVehicule(vehicule);
        }
        new ArrayList<>(vehicule.getEquipements()).forEach(vehicule::removeEquipement);
        vehiculeRepository.delete(vehicule);
    }

    /** Ajoute une ligne dans vehicule_equipement (côté propriétaire). */
    @Override
    @Transactional
    public Vehicule affecterEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = getVehicule(vehiculeId);
        Equipement equipement = getEquipement(equipementId);
        if (vehicule.getEquipements().contains(equipement)) {
            throw new BusinessRuleException(
                    "L'équipement " + equipementId + " est déjà affecté au véhicule " + vehiculeId);
        }
        vehicule.addEquipement(equipement);
        return chargerAssociations(vehicule);
    }

    /** Supprime la ligne de vehicule_equipement ; l'équipement est conservé. */
    @Override
    @Transactional
    public void desaffecterEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = getVehicule(vehiculeId);
        Equipement equipement = getEquipement(equipementId);
        if (!vehicule.getEquipements().contains(equipement)) {
            throw new BusinessRuleException(
                    "L'équipement " + equipementId + " n'est pas affecté au véhicule " + vehiculeId);
        }
        vehicule.removeEquipement(equipement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> findEquipements(Long vehiculeId) {
        return List.copyOf(getVehicule(vehiculeId).getEquipements());
    }

    private Vehicule getVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    private Equipement getEquipement(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }

    private static Vehicule chargerAssociations(Vehicule vehicule) {
        Hibernate.initialize(vehicule.getAgence());
        Hibernate.initialize(vehicule.getEquipements());
        Hibernate.initialize(vehicule.getMaintenances());
        return vehicule;
    }
}
