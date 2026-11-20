package tn.esprit.autoloc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.service.IEquipementService;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;

    public EquipementServiceImpl(IEquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipement> findAll() {
        return equipementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Equipement findById(Long id) {
        return getEquipement(id);
    }

    @Override
    @Transactional
    public Equipement create(Equipement equipement) {
        equipement.setIdEquipement(null);
        return equipementRepository.save(equipement);
    }

    @Override
    @Transactional
    public Equipement update(Long id, Equipement equipement) {
        Equipement existant = getEquipement(id);
        existant.setLibelle(equipement.getLibelle());
        return existant;
    }

    /** Retire d'abord l'équipement de chaque véhicule (côté propriétaire) : lignes de jointure supprimées. */
    @Override
    @Transactional
    public void delete(Long id) {
        Equipement equipement = getEquipement(id);
        new ArrayList<>(equipement.getVehicules()).forEach(v -> v.removeEquipement(equipement));
        equipementRepository.delete(equipement);
    }

    private Equipement getEquipement(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }
}
