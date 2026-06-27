-- =========================================
-- CLEAN (Optional but safe)
-- =========================================
SET SQL_SAFE_UPDATES = 0;
DELETE FROM daily_log_source_breakdown;
DELETE FROM slab_monthly_summary;
DELETE FROM daily_log;
DELETE FROM apartment_source_config;
DELETE FROM water_rate;
DELETE FROM water_source;
DELETE FROM apartment;
DELETE FROM apartment_type;
DELETE FROM block;

-- =========================================
-- BLOCKS
-- =========================================
INSERT INTO block (id, name) VALUES
(1, 'Azure Heights'),
(2, 'Crimson Towers'),
(3, 'Emerald Residency');

-- =========================================
-- APARTMENT TYPES
-- =========================================
INSERT INTO apartment_type (id, name, base_occupancy, litres_per_person) VALUES
(1, 'Studio', 1, 100),
(2, '2BHK', 4, 135),
(3, '3BHK', 5, 155),
(4, 'Penthouse', 7, 200);

-- =========================================
-- APARTMENTS
-- =========================================
INSERT INTO apartment (id, number, block_id, type_id) VALUES
(1, 'A-101', 1, 2),
(2, 'A-102', 1, 3),
(3, 'B-201', 2, 1),
(4, 'C-301', 3, 4);

-- =========================================
-- WATER SOURCES
-- =========================================
INSERT INTO water_source (id, name, pricing_type, supply_type) VALUES
(1, 'City Water', 'SLAB', 'MUNICIPAL'),
(2, 'Tanker', 'SLAB', 'PRIVATE'),
(3, 'Borewell', 'SLAB', 'GROUND');

-- =========================================
-- WATER RATES (CITY)
-- =========================================
INSERT INTO water_rate
(id, min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES
(1, 0, 500, 1, '2026-01-01', '2026-12-31', 1),
(2, 501, 1000, 2, '2026-01-01', '2026-12-31', 1),
(3, 1001, 999999, 4, '2026-01-01', '2026-12-31', 1);

-- =========================================
-- WATER RATES (TANKER)
-- =========================================
INSERT INTO water_rate
(id, min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES
(4, 0, 500, 5, '2026-01-01', '2026-12-31', 2),
(5, 501, 1000, 7, '2026-01-01', '2026-12-31', 2),
(6, 1001, 999999, 10, '2026-01-01', '2026-12-31', 2);

-- =========================================
-- WATER RATES (BOREWELL)
-- =========================================
INSERT INTO water_rate
(id, min_litres, max_litres, rate_per_litre, effective_from, effective_to, source_id)
VALUES
(7, 0, 500, 2, '2026-01-01', '2026-12-31', 3),
(8, 501, 1000, 3, '2026-01-01', '2026-12-31', 3),
(9, 1001, 999999, 5, '2026-01-01', '2026-12-31', 3);

-- =========================================
-- SOURCE CONFIGS
-- =========================================
INSERT INTO apartment_source_config
(id, ratio_percent, apartment_id, source_id)
VALUES
(1, 70, 1, 1),
(2, 30, 1, 2),
(3, 50, 2, 1),
(4, 50, 2, 3),
(5, 100, 3, 3),
(6, 60, 4, 1),
(7, 40, 4, 2);

-- =========================================
-- DAILY LOGS
-- =========================================
INSERT INTO daily_log
(id, log_date, total_litres_consumed, guest_count, day_cost, apartment_id)
VALUES
(1, '2026-04-01', 900, 1, 0, 1),
(2, '2026-04-02', 1500, 3, 0, 1),
(3, '2026-04-01', 1200, 0, 0, 2),
(4, '2026-04-02', 2200, 2, 0, 2),
(5, '2026-04-01', 300, 0, 0, 3),
(6, '2026-04-01', 2500, 5, 0, 4);
