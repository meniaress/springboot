-- =====================================================================
-- AutoLoc - Jeu de données de test
-- =====================================================================
USE autoloc;

INSERT INTO agence (nom, ville, adresse, telephone) VALUES
 ('AutoLoc Tunis Centre', 'Tunis',  '12 Av. Habib Bourguiba', '71000001'),
 ('AutoLoc Ariana',       'Ariana', '5 Rue de la Liberté',    '71000002'),
 ('AutoLoc Sousse',       'Sousse', '20 Bd du 14 Janvier',    '73000003');

INSERT INTO employe (nom, prenom, role, id_agence) VALUES
 ('Ben Salah', 'Karim', 'MANAGER', 1),
 ('Trabelsi',  'Sonia', 'AGENT',   1),
 ('Gharbi',    'Anis',  'MANAGER', 2),
 ('Jaziri',    'Leila', 'AGENT',   2),
 ('Mansouri',  'Hedi',  'AGENT',   3);

INSERT INTO vehicule (immatriculation, marque, modele, categorie, tarif_journalier, statut, id_agence) VALUES
 ('123 TUN 4567', 'Renault', 'Clio 5',     'CITADINE',   80.00,  'DISPONIBLE', 1),
 ('234 TUN 5678', 'Peugeot', '508',        'BERLINE',    150.00, 'LOUE',       1),
 ('345 TUN 6789', 'Hyundai', 'Tucson',     'SUV',        190.00, 'DISPONIBLE', 2),
 ('456 TUN 7890', 'Renault', 'Kangoo',     'UTILITAIRE', 110.00, 'MAINTENANCE',2),
 ('567 TUN 8901', 'Kia',     'Picanto',    'CITADINE',   70.00,  'DISPONIBLE', 3);

INSERT INTO equipement (libelle) VALUES
 ('GPS'), ('Siège bébé'), ('Climatisation'), ('Bluetooth'), ('Porte-bagages');

INSERT INTO vehicule_equipement (id_vehicule, id_equipement) VALUES
 (1,3),(1,4),
 (2,1),(2,3),(2,4),
 (3,1),(3,2),(3,3),
 (4,5),
 (5,3);

INSERT INTO maintenance (date_debut, date_fin, description, id_vehicule) VALUES
 ('2026-09-01', '2026-09-03', 'Vidange et filtres',          4),
 ('2026-10-10', NULL,         'Remplacement plaquettes frein', 4),
 ('2026-08-15', '2026-08-16', 'Contrôle technique',          2);

INSERT INTO client (nom, prenom, email, telephone, num_permis, date_inscription) VALUES
 ('Hamdi',   'Yassine', 'yassine.hamdi@mail.tn', '20111222', 'P-100234', '2026-01-15'),
 ('Bouzid',  'Ines',    'ines.bouzid@mail.tn',   '22333444', 'P-100567', '2026-03-02'),
 ('Chaabane','Omar',    'omar.chaabane@mail.tn', '55666777', 'P-100890', '2026-06-20');

INSERT INTO reservation (date_debut, date_fin, statut, id_client, id_vehicule) VALUES
 ('2026-10-01', '2026-10-07', 'TERMINEE',  1, 1),
 ('2026-10-14', '2026-10-20', 'CONFIRMEE', 2, 2),
 ('2026-10-25', '2026-10-28', 'EN_ATTENTE',3, 3),
 ('2026-09-10', '2026-09-12', 'ANNULEE',   1, 5);

INSERT INTO contrat (date_signature, montant_total, valide, id_reservation) VALUES
 ('2026-09-30', 480.00, 0, 1),
 ('2026-10-13', 900.00, 1, 2);

INSERT INTO paiement (montant, date_paiement, mode_paiement, contrat_id_contrat) VALUES
 (200.00, '2026-09-30', 'CARTE',    1),
 (280.00, '2026-10-07', 'ESPECES',  1),
 (450.00, '2026-10-13', 'VIREMENT', 2),
 (450.00, '2026-10-20', 'CARTE',    2);
