package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
