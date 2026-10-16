USE autoloc;

-- Tables et clés étrangères générées
SHOW TABLES;
SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'autoloc' AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME;

-- Paiements par contrat
SELECT c.id_contrat, c.montant_total, SUM(p.montant) AS total_paye
FROM contrat c LEFT JOIN paiement p ON p.contrat_id_contrat = c.id_contrat
GROUP BY c.id_contrat, c.montant_total;

-- Équipements par véhicule
SELECT v.immatriculation, GROUP_CONCAT(e.libelle ORDER BY e.libelle) AS equipements
FROM vehicule v
LEFT JOIN vehicule_equipement ve ON ve.id_vehicule = v.id_vehicule
LEFT JOIN equipement e ON e.id_equipement = ve.id_equipement
GROUP BY v.id_vehicule, v.immatriculation;
