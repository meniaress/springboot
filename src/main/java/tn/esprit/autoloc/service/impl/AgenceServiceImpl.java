package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IAgenceService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IEmployeRepository employeRepository;
    private final IVehiculeRepository vehiculeRepository;

    public AgenceServiceImpl(IAgenceRepository agenceRepository,
                             IEmployeRepository employeRepository,
                             IVehiculeRepository vehiculeRepository) {
        this.agenceRepository = agenceRepository;
        this.employeRepository = employeRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agence> findAll() {
        List<Agence> agences = agenceRepository.findAll();
        agences.forEach(AgenceServiceImpl::chargerCollections);
        return agences;
    }

    @Override
    @Transactional(readOnly = true)
    public Agence findById(Long id) {
        return chargerCollections(getAgence(id));
    }

    @Override
    @Transactional
    public Agence create(Agence agence) {
        agence.setIdAgence(null);
        return agenceRepository.save(agence);
    }

    @Override
    @Transactional
    public Agence update(Long id, Agence agence) {
        Agence existante = getAgence(id);
        existante.setNom(agence.getNom());
        existante.setVille(agence.getVille());
        existante.setAdresse(agence.getAdresse());
        existante.setTelephone(agence.getTelephone());
        return chargerCollections(existante);
    }

    /** Les employés et véhicules sont désaffectés (FK mise à NULL), jamais supprimés. */
    @Override
    @Transactional
    public void delete(Long id) {
        Agence agence = getAgence(id);
        new ArrayList<>(agence.getEmployes()).forEach(agence::removeEmploye);
        new ArrayList<>(agence.getVehicules()).forEach(agence::removeVehicule);
        agenceRepository.delete(agence);
    }

    @Override
    @Transactional
    public Agence affecterEmploye(Long agenceId, Long employeId) {
        Agence agence = getAgence(agenceId);
        Employe employe = getEmploye(employeId);
        if (employe.getAgence() != null) {
            Long actuelle = employe.getAgence().getIdAgence();
            throw new BusinessRuleException(actuelle.equals(agenceId)
                    ? "L'employé " + employeId + " est déjà affecté à l'agence " + agenceId
                    : "L'employé " + employeId + " est déjà affecté à l'agence " + actuelle
                      + " : le désaffecter avant de le réaffecter");
        }
        agence.addEmploye(employe);
        return chargerCollections(agence);
    }

    @Override
    @Transactional
    public void desaffecterEmploye(Long agenceId, Long employeId) {
        Agence agence = getAgence(agenceId);
        Employe employe = getEmploye(employeId);
        if (employe.getAgence() == null || !employe.getAgence().getIdAgence().equals(agenceId)) {
            throw new BusinessRuleException(
                    "L'employé " + employeId + " n'est pas affecté à l'agence " + agenceId);
        }
        agence.removeEmploye(employe);
    }

    @Override
    @Transactional
    public Agence affecterVehicule(Long agenceId, Long vehiculeId) {
        Agence agence = getAgence(agenceId);
        Vehicule vehicule = getVehicule(vehiculeId);
        if (vehicule.getAgence() != null) {
            Long actuelle = vehicule.getAgence().getIdAgence();
            throw new BusinessRuleException(actuelle.equals(agenceId)
                    ? "Le véhicule " + vehiculeId + " est déjà affecté à l'agence " + agenceId
                    : "Le véhicule " + vehiculeId + " appartient à l'agence " + actuelle
                      + " : utiliser le transfert");
        }
        agence.addVehicule(vehicule);
        return chargerCollections(agence);
    }

    @Override
    @Transactional
    public void desaffecterVehicule(Long agenceId, Long vehiculeId) {
        Agence agence = getAgence(agenceId);
        Vehicule vehicule = getVehicule(vehiculeId);
        if (vehicule.getAgence() == null || !vehicule.getAgence().getIdAgence().equals(agenceId)) {
            throw new BusinessRuleException(
                    "Le véhicule " + vehiculeId + " n'est pas affecté à l'agence " + agenceId);
        }
        agence.removeVehicule(vehicule);
    }

    /** Atomique : si l'agence cible n'existe pas, le retrait de l'agence d'origine est annulé (rollback). */
    @Override
    @Transactional
    public Vehicule transfererVehicule(Long vehiculeId, Long agenceCibleId) {
        Vehicule vehicule = getVehicule(vehiculeId);
        Agence origine = vehicule.getAgence();
        if (origine == null) {
            throw new BusinessRuleException("Le véhicule " + vehiculeId
                    + " n'est affecté à aucune agence : utiliser l'affectation");
        }
        if (origine.getIdAgence().equals(agenceCibleId)) {
            throw new BusinessRuleException("Le véhicule " + vehiculeId
                    + " est déjà dans l'agence " + agenceCibleId);
        }
        origine.removeVehicule(vehicule);
        Agence cible = getAgence(agenceCibleId);
        cible.addVehicule(vehicule);
        return vehicule;
    }

    private Agence getAgence(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    private Employe getEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    private Vehicule getVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    /** Charge les collections LAZY dans la transaction pour qu'elles soient sérialisées. */
    private static Agence chargerCollections(Agence agence) {
        Hibernate.initialize(agence.getEmployes());
        Hibernate.initialize(agence.getVehicules());
        return agence;
    }
}
