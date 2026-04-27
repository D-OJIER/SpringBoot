-- =========================
-- BLOCK
-- =========================
INSERT INTO block (id, name) VALUES (1, 'A Block');
INSERT INTO block (id, name) VALUES (2, 'B Block');

-- =========================
-- APARTMENT TYPE
-- =========================
INSERT INTO apartment_type (id, name, base_occupancy, litres_per_person)
VALUES (1, '2BHK', 4, 135);

INSERT INTO apartment_type (id, name, base_occupancy, litres_per_person)
VALUES (2, '3BHK', 5, 155);

-- =========================
-- APARTMENT
-- =========================
INSERT INTO apartment (id, number, block_id, type_id)
VALUES (1, 'A-101', 1, 1);

INSERT INTO apartment (id, number, block_id, type_id)
VALUES (2, 'A-102', 1, 2);

-- =========================
-- WATER SOURCE
-- =========================
INSERT INTO water_source (id, name, pricing_type, supply_type)
VALUES (1, 'City Water', 'SLAB', 'MUNICIPAL');

INSERT INTO water_source (id, name, pricing_type, supply_type)
VALUES (2, 'Tanker', 'SLAB', 'PRIVATE');

-- =========================
-- WATER RATE (City Water)
-- =========================
INSERT INTO water_rate ( min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES ( 0, 500, 1, '2026-01-01', '2026-12-31', 1);

INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES ( 501, 1000, 2, '2026-01-01', '2026-12-31', 1);

INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES ( 1001, 999999, 3, '2026-01-01', '2026-12-31', 1);

-- =========================
-- WATER RATE (Tanker)
-- =========================
INSERT INTO water_rate ( min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES ( 0, 500, 5, '2026-01-01', '2026-12-31', 2);

INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES ( 501, 1000, 7, '2026-01-01', '2026-12-31', 2);

INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES ( 1001, 999999, 10, '2026-01-01', '2026-12-31', 2);