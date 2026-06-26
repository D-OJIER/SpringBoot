-- property-service seed data

INSERT INTO block (id, name) VALUES
(1, 'Azure Heights'),
(2, 'Crimson Towers'),
(3, 'Emerald Residency');

INSERT INTO apartment_type (id, name, base_occupancy, litres_per_person) VALUES
(1, 'Studio', 1, 100),
(2, '2BHK', 4, 135),
(3, '3BHK', 5, 155),
(4, 'Penthouse', 7, 200);

INSERT INTO apartment (id, number, block_id, type_id) VALUES
(1, 'A-101', 1, 2),
(2, 'A-102', 1, 3),
(3, 'B-201', 2, 1),
(4, 'C-301', 3, 4);
