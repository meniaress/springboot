package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IMaintenanceService;

import java.util.List;

@Service
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;

    public MaintenanceServiceImpl(IMaintenanceRepository maintenanceRepository,
                                  IVehiculeRepository vehiculeRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> findAll() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        maintenances.forEach(m -> Hibernate.initialize(m.getVehicule()));
        return maintenances;
    }

    @Override
    @Transactional(readOnly = true)
    public Maintenance findById(Long id) {
        Maintenance maintenance = getMaintenance(id);
        Hibernate.initialize(maintenance.getVehicule());
        return maintenance;
    }

    /** Aucun save : INSERT envoyé au commit par cascade PERSIST depuis le véhicule. */
    @Override
    @Transactional
    public Maintenance planifierMaintenance(Long vehiculeId, Maintenance maintenance) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
        verifierDates(maintenance);
        maintenance.setIdMaintenance(null);
        vehicule.addMaintenance(maintenance);
        return maintenance;
    }

    @Override
    @Transactional
    public Maintenance update(Long id, Maintenance maintenance) {
        Maintenance existante = getMaintenance(id);
        verifierDates(maintenance);
        existante.setDateDebut(maintenance.getDateDebut());
        existante.setDateFin(maintenance.getDateFin());
        existante.setDescription(maintenance.getDescription());
        Hibernate.initialize(existante.getVehicule());
        return existante;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Maintenance maintenance = getMaintenance(id);
        if (maintenance.getVehicule() != null) {
            maintenance.getVehicule().removeMaintenance(maintenance);
        }
        maintenanceRepository.delete(maintenance);
    }

    private Maintenance getMaintenance(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    private static void verifierDates(Maintenance maintenance) {
        if (maintenance.getDateDebut() != null && maintenance.getDateFin() != null
                && maintenance.getDateFin().isBefore(maintenance.getDateDebut())) {
            throw new BusinessRuleException("La date de fin précède la date de début");
        }
    }
}
