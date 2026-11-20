package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IReservationService;

import java.util.List;

@Service
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;

    public ReservationServiceImpl(IReservationRepository reservationRepository,
                                  IClientRepository clientRepository,
                                  IVehiculeRepository vehiculeRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        reservations.forEach(ReservationServiceImpl::chargerAssociations);
        return reservations;
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return chargerAssociations(getReservation(id));
    }

    @Override
    @Transactional
    public Reservation creerReservation(Long clientId, Long vehiculeId, Reservation reservation) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
        verifierDates(reservation);
        reservation.setIdReservation(null);
        reservation.setVehicule(vehicule);
        client.addReservation(reservation);
        return chargerAssociations(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public Reservation update(Long id, Reservation reservation) {
        Reservation existante = getReservation(id);
        verifierDates(reservation);
        existante.setDateDebut(reservation.getDateDebut());
        existante.setDateFin(reservation.getDateFin());
        existante.setStatut(reservation.getStatut());
        return chargerAssociations(existante);
    }

    /** cascade ALL vers le contrat, lui-même en cascade vers ses paiements. */
    @Override
    @Transactional
    public void delete(Long id) {
        Reservation reservation = getReservation(id);
        if (reservation.getClient() != null) {
            reservation.getClient().removeReservation(reservation);
        }
        reservationRepository.delete(reservation);
    }

    private Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    private static void verifierDates(Reservation reservation) {
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null
                && reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new BusinessRuleException("La date de fin précède la date de début");
        }
    }

    private static Reservation chargerAssociations(Reservation reservation) {
        Hibernate.initialize(reservation.getClient());
        Hibernate.initialize(reservation.getVehicule());
        return reservation;
    }
}
