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
