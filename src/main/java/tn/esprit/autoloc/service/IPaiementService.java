package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Paiement;

import java.util.List;

/** Lecture seule : un paiement ne se crée et ne se retire qu'à travers son contrat (IContratService). */
public interface IPaiementService {

    List<Paiement> findAll();

    Paiement findById(Long id);
}
