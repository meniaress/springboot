package tn.esprit.autoloc.service.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.exception.BusinessRuleException;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.service.IClientService;

import java.util.List;

@Service
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;

    public ClientServiceImpl(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        List<Client> clients = clientRepository.findAll();
        clients.forEach(c -> Hibernate.initialize(c.getReservations()));
        return clients;
    }

    @Override
    @Transactional(readOnly = true)
    public Client findById(Long id) {
        Client client = getClient(id);
        Hibernate.initialize(client.getReservations());
        return client;
    }

    @Override
    @Transactional
    public Client create(Client client) {
        client.setIdClient(null);
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client update(Long id, Client client) {
        Client existant = getClient(id);
        existant.setNom(client.getNom());
        existant.setPrenom(client.getPrenom());
        existant.setEmail(client.getEmail());
        existant.setTelephone(client.getTelephone());
        existant.setNumPermis(client.getNumPermis());
        existant.setDateInscription(client.getDateInscription());
        Hibernate.initialize(existant.getReservations());
        return existant;
    }

    /** L'historique des réservations ne doit pas disparaître avec le client. */
    @Override
    @Transactional
    public void delete(Long id) {
        Client client = getClient(id);
        if (!client.getReservations().isEmpty()) {
            throw new BusinessRuleException("Le client " + id + " possède des réservations : suppression refusée");
        }
        clientRepository.delete(client);
    }

    private Client getClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }
}
