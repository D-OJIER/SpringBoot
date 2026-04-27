-- =========================
-- CLEAN (optional but safe)
-- =========================
DELETE FROM daily_log;
DELETE FROM apartment_source_config;
DELETE FROM water_rate;
DELETE FROM water_source;
DELETE FROM apartment;
DELETE FROM apartment_type;
DELETE FROM block;

-- =========================
-- BLOCK
-- =========================
INSERT INTO block (id, name) VALUES (1, 'A Block');

-- =========================
-- APARTMENT TYPE
-- =========================
INSERT INTO apartment_type (id, name, base_occupancy, litres_per_person)
VALUES (1, '2BHK', 4, 135);

-- =========================
-- APARTMENT
-- =========================
INSERT INTO apartment (id, number, block_id, type_id)
VALUES (1, 'A-101', 1, 1);

-- =========================
-- WATER SOURCE
-- =========================
INSERT INTO water_source (id, name, pricing_type, supply_type)
VALUES (1, 'City Water', 'SLAB', 'MUNICIPAL');

INSERT INTO water_source (id, name, pricing_type, supply_type)
VALUES (2, 'Tanker', 'SLAB', 'PRIVATE');

-- =========================
-- WATER RATE
-- =========================
INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES (0, 500, 1, '2026-01-01', '2026-12-31', 1);

INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES (501, 1000, 2, '2026-01-01', '2026-12-31', 1);

INSERT INTO water_rate (min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES (0, 500, 5, '2026-01-01', '2026-12-31', 2);

-- =========================
-- APARTMENT SOURCE CONFIG
-- =========================
INSERT INTO apartment_source_config (ratio_percent, apartment_id, source_id)
VALUES (60, 1, 1);

INSERT INTO apartment_source_config (ratio_percent, apartment_id, source_id)
VALUES (40, 1, 2);

-- =========================
-- DAILY LOG (NEW)
-- =========================
INSERT INTO daily_log (log_date, total_litres_consumed, guest_count, day_cost, apartment_id)
VALUES ('2026-04-01', 1000, 2, 0, 1);