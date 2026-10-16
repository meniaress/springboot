-- =====================================================================
-- AutoLoc - Atelier 2 : schéma MySQL équivalent à celui généré par Hibernate
-- (ddl-auto=update, convention de nommage Spring : camelCase -> snake_case)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS autoloc
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE autoloc;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS paiement;
DROP TABLE IF EXISTS contrat;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS maintenance;
DROP TABLE IF EXISTS vehicule_equipement;
DROP TABLE IF EXISTS equipement;
DROP TABLE IF EXISTS vehicule;
DROP TABLE IF EXISTS employe;
DROP TABLE IF EXISTS agence;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
CREATE TABLE agence (
    id_agence   BIGINT       NOT NULL AUTO_INCREMENT,
    nom         VARCHAR(255),
    ville       VARCHAR(255),
    adresse     VARCHAR(255),
    telephone   VARCHAR(255),
    PRIMARY KEY (id_agence)
) ENGINE=InnoDB;

CREATE TABLE employe (
    id_employe  BIGINT       NOT NULL AUTO_INCREMENT,
    nom         VARCHAR(255),
    prenom      VARCHAR(255),
    role        ENUM('AGENT','MANAGER'),
    id_agence   BIGINT,
    PRIMARY KEY (id_employe),
    CONSTRAINT fk_employe_agence
        FOREIGN KEY (id_agence) REFERENCES agence (id_agence)
) ENGINE=InnoDB;

CREATE TABLE vehicule (
    id_vehicule      BIGINT         NOT NULL AUTO_INCREMENT,
    immatriculation  VARCHAR(255),
    marque           VARCHAR(255),
    modele           VARCHAR(255),
    categorie        ENUM('CITADINE','BERLINE','SUV','UTILITAIRE'),
    tarif_journalier DECIMAL(38,2),
    statut           ENUM('DISPONIBLE','LOUE','MAINTENANCE'),
    id_agence        BIGINT,
    PRIMARY KEY (id_vehicule),
    CONSTRAINT fk_vehicule_agence
        FOREIGN KEY (id_agence) REFERENCES agence (id_agence)
) ENGINE=InnoDB;

CREATE TABLE equipement (
    id_equipement BIGINT       NOT NULL AUTO_INCREMENT,
    libelle       VARCHAR(255),
    PRIMARY KEY (id_equipement)
) ENGINE=InnoDB;

-- Table de jointure ManyToMany Vehicule <-> Equipement
CREATE TABLE vehicule_equipement (
    id_vehicule   BIGINT NOT NULL,
    id_equipement BIGINT NOT NULL,
    PRIMARY KEY (id_vehicule, id_equipement),
    CONSTRAINT fk_ve_vehicule
        FOREIGN KEY (id_vehicule)   REFERENCES vehicule (id_vehicule),
    CONSTRAINT fk_ve_equipement
        FOREIGN KEY (id_equipement) REFERENCES equipement (id_equipement)
) ENGINE=InnoDB;

CREATE TABLE maintenance (
    id_maintenance BIGINT       NOT NULL AUTO_INCREMENT,
    date_debut     DATE,
    date_fin       DATE,
    description    VARCHAR(255),
    id_vehicule    BIGINT,
    PRIMARY KEY (id_maintenance),
    CONSTRAINT fk_maintenance_vehicule
        FOREIGN KEY (id_vehicule) REFERENCES vehicule (id_vehicule)
) ENGINE=InnoDB;

CREATE TABLE client (
    id_client        BIGINT       NOT NULL AUTO_INCREMENT,
    nom              VARCHAR(255),
    prenom           VARCHAR(255),
    email            VARCHAR(255),
    telephone        VARCHAR(255),
    num_permis       VARCHAR(255),
    date_inscription DATE,
    PRIMARY KEY (id_client)
) ENGINE=InnoDB;

CREATE TABLE reservation (
    id_reservation BIGINT       NOT NULL AUTO_INCREMENT,
    date_debut     DATE,
    date_fin       DATE,
    statut         ENUM('EN_ATTENTE','CONFIRMEE','ANNULEE','TERMINEE'),
    id_client      BIGINT,
    id_vehicule    BIGINT,
    PRIMARY KEY (id_reservation),
    CONSTRAINT fk_reservation_client
        FOREIGN KEY (id_client)   REFERENCES client (id_client),
    CONSTRAINT fk_reservation_vehicule
        FOREIGN KEY (id_vehicule) REFERENCES vehicule (id_vehicule)
) ENGINE=InnoDB;

CREATE TABLE contrat (
    id_contrat      BIGINT         NOT NULL AUTO_INCREMENT,
    date_signature  DATE,
    montant_total   DECIMAL(38,2),
    valide          BIT            NOT NULL,
    id_reservation  BIGINT,
    PRIMARY KEY (id_contrat),
    CONSTRAINT uk_contrat_reservation UNIQUE (id_reservation),
    CONSTRAINT fk_contrat_reservation
        FOREIGN KEY (id_reservation) REFERENCES reservation (id_reservation)
) ENGINE=InnoDB;

CREATE TABLE paiement (
    id_paiement      BIGINT         NOT NULL AUTO_INCREMENT,
    montant          DECIMAL(38,2),
    date_paiement    DATE,
    mode_paiement    ENUM('CARTE','ESPECES','VIREMENT'),
    contrat_id_contrat BIGINT,       -- nom par défaut généré par Hibernate
    PRIMARY KEY (id_paiement),
    CONSTRAINT fk_paiement_contrat
        FOREIGN KEY (contrat_id_contrat) REFERENCES contrat (id_contrat)
        ON DELETE CASCADE              -- équivalent SQL de CascadeType.REMOVE (composition)
) ENGINE=InnoDB;
