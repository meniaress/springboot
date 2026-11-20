package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.service.IEmployeService;

import java.util.List;

@Service
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;

    public EmployeServiceImpl(IEmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employe> findAll() {
        List<Employe> employes = employeRepository.findAll();
        employes.forEach(e -> Hibernate.initialize(e.getAgence()));
        return employes;
    }

    @Override
    @Transactional(readOnly = true)
    public Employe findById(Long id) {
        Employe employe = getEmploye(id);
        Hibernate.initialize(employe.getAgence());
        return employe;
    }

    @Override
    @Transactional
    public Employe create(Employe employe) {
        employe.setIdEmploye(null);
        return employeRepository.save(employe);
    }

    @Override
    @Transactional
    public Employe update(Long id, Employe employe) {
        Employe existant = getEmploye(id);
        existant.setNom(employe.getNom());
        existant.setPrenom(employe.getPrenom());
        existant.setRole(employe.getRole());
        Hibernate.initialize(existant.getAgence());
        return existant;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employe employe = getEmploye(id);
        if (employe.getAgence() != null) {
            employe.getAgence().removeEmploye(employe);
        }
        employeRepository.delete(employe);
    }

    private Employe getEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }
}
