package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IPaiementRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@Service
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final IPaiementRepository paiementRepository;

    public ContratServiceImpl(IContratRepository contratRepository,
                              IReservationRepository reservationRepository,
                              IPaiementRepository paiementRepository) {
        this.contratRepository = contratRepository;
        this.reservationRepository = reservationRepository;
        this.paiementRepository = paiementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        List<Contrat> contrats = contratRepository.findAll();
        contrats.forEach(ContratServiceImpl::chargerAssociations);
        return contrats;
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return chargerAssociations(getContrat(id));
    }

    /** Ne modifie que les attributs du contrat ; les paiements passent par ajouter/retirerPaiement. */
    @Override
    @Transactional
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = getContrat(id);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        return chargerAssociations(existant);
    }

    /** cascade REMOVE : DELETE des paiements puis du contrat. */
    @Override
    @Transactional
    public void delete(Long id) {
        Contrat contrat = getContrat(id);
        if (contrat.getReservation() != null) {
            contrat.getReservation().retirerContrat();
        }
        contratRepository.delete(contrat);
    }

    /**
     * Un INSERT du contrat puis un INSERT par paiement (cascade PERSIST).
     * Un paiement invalide lève une ConstraintViolationException : rollback complet.
     */
    @Override
    @Transactional
    public Contrat creerContrat(Long reservationId, Contrat contrat) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
        if (reservation.getContrat() != null) {
            throw new BusinessRuleException("La réservation " + reservationId
                    + " possède déjà un contrat (id = " + reservation.getContrat().getIdContrat() + ")");
        }
        contrat.setIdContrat(null);
        contrat.getPaiements().forEach(p -> p.setIdPaiement(null));
        reservation.etablirContrat(contrat);
        return contratRepository.save(contrat);
    }

    /** Aucun save : le paiement est inséré au commit par cascade depuis le contrat géré. */
    @Override
    @Transactional
    public Paiement ajouterPaiement(Long contratId, Paiement paiement) {
        Contrat contrat = getContrat(contratId);
        paiement.setIdPaiement(null);
        contrat.addPaiement(paiement);
        return paiement;
    }

    /** orphanRemoval : le paiement retiré de la collection est supprimé en base. */
    @Override
    @Transactional
    public void retirerPaiement(Long contratId, Long paiementId) {
        Contrat contrat = getContrat(contratId);
        Paiement paiement = contrat.getPaiements().stream()
                .filter(p -> p.getIdPaiement().equals(paiementId))
                .findFirst()
                .orElseThrow(() -> paiementRepository.existsById(paiementId)
                        ? new BusinessRuleException("Le paiement " + paiementId
                                + " n'appartient pas au contrat " + contratId)
                        : new ResourceNotFoundException("Paiement", paiementId));
        contrat.removePaiement(paiement);
    }

    private Contrat getContrat(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    private static Contrat chargerAssociations(Contrat contrat) {
        Hibernate.initialize(contrat.getReservation());
        Hibernate.initialize(contrat.getPaiements());
        return contrat;
    }
}
