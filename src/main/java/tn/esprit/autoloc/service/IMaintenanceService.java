package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Maintenance;

import java.util.List;

public interface IMaintenanceService {

    List<Maintenance> findAll();

    Maintenance findById(Long id);

    Maintenance planifierMaintenance(Long vehiculeId, Maintenance maintenance);

    Maintenance update(Long id, Maintenance maintenance);

    void delete(Long id);
}
