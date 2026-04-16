-- =========================
-- 🏢 BLOCK
-- =========================
INSERT INTO block (id, name) VALUES (1, 'Valhalla');

-- =========================
-- 🏠 APARTMENT TYPES
-- =========================
INSERT INTO apartment_type (id, type_name, base_occupancy, litres_per_person)
VALUES (1, '2BHK', 3, 135);

INSERT INTO apartment_type (id, type_name, base_occupancy, litres_per_person)
VALUES (2, '3BHK', 5, 150);

-- =========================
-- 🏠 APARTMENTS
-- =========================
INSERT INTO apartment (id, apartment_number, status, block_id, type_id)
VALUES (1, 'A101', 'ACTIVE', 1, 1);

INSERT INTO apartment (id, apartment_number, status, block_id, type_id)
VALUES (2, 'A102', 'ACTIVE', 1, 1);

INSERT INTO apartment (id, apartment_number, status, block_id, type_id)
VALUES (3, 'B201', 'ACTIVE', 1, 2);

INSERT INTO apartment (id, apartment_number, status, block_id, type_id)
VALUES (4, 'C301', 'ACTIVE', 1, 1);

-- =========================
-- 👤 USERS
-- =========================
INSERT INTO users (id, username, password, role, apartment_id)
VALUES (1, 'thor', '123', 'USER', 1);

INSERT INTO users (id, username, password, role, apartment_id)
VALUES (2, 'loki', '123', 'USER', 2);

INSERT INTO users (id, username, password, role, apartment_id)
VALUES (3, 'odin', '123', 'USER', 3);

INSERT INTO users (id, username, password, role, apartment_id)
VALUES (4, 'freya', '123', 'USER', 4);

-- =========================
-- 💧 WATER SOURCES
-- =========================
INSERT INTO water_source (id, name, type) VALUES (1, 'City', 'VARIABLE');
INSERT INTO water_source (id, name, type) VALUES (2, 'Borewell', 'VARIABLE');
INSERT INTO water_source (id, name, type) VALUES (3, 'Tanker', 'VARIABLE');

-- =========================
-- 💰 WATER RATES
-- =========================
INSERT INTO water_rate (id, min_litres, max_litres, rate_per_litre, source_id)
VALUES (1, 0, 500, 0.5, 1);

INSERT INTO water_rate (id, min_litres, max_litres, rate_per_litre, source_id)
VALUES (2, 500, 1500, 1.0, 1);

INSERT INTO water_rate (id, min_litres, max_litres, rate_per_litre, source_id)
VALUES (3, 1500, 3000, 1.5, 1);

INSERT INTO water_rate (id, min_litres, max_litres, rate_per_litre, source_id)
VALUES (4, 0, 3000, 0.2, 2);

INSERT INTO water_rate (id, min_litres, max_litres, rate_per_litre, source_id)
VALUES (5, 0, 500, 2.0, 3);

INSERT INTO water_rate (id, min_litres, max_litres, rate_per_litre, source_id)
VALUES (6, 500, 2000, 3.0, 3);

-- =========================
-- ⚖️ SOURCE CONFIG
-- =========================
INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (1, 1, 1, 33.33);

INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (2, 1, 2, 66.67);

INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (3, 2, 1, 30);

INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (4, 2, 2, 70);

INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (5, 3, 1, 50);

INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (6, 3, 3, 50);

INSERT INTO apartment_source_config (id, apartment_id, source_id, ratio_percent)
VALUES (7, 4, 2, 100);

-- =========================
-- 👥 GUESTS
-- =========================
INSERT INTO guest_stay (id, apartment_id, check_in_date, check_out_date, guest_count)
VALUES (1, 2, CURRENT_DATE, CURRENT_DATE + 5, 2);

INSERT INTO guest_stay (id, apartment_id, check_in_date, check_out_date, guest_count)
VALUES (2, 2, CURRENT_DATE, CURRENT_DATE + 5, 3);

INSERT INTO guest_stay (id, apartment_id, check_in_date, check_out_date, guest_count)
VALUES (3, 3, CURRENT_DATE, CURRENT_DATE + 5, 6);

-- =========================
-- 📊 DAILY LOGS
-- =========================
INSERT INTO daily_log (id, apartment_id, log_date, total_litres_consumed, guest_count, day_cost)
VALUES (1, 1, CURRENT_DATE, 900, 0, 0);

INSERT INTO daily_log (id, apartment_id, log_date, total_litres_consumed, guest_count, day_cost)
VALUES (2, 2, CURRENT_DATE, 2400, 0, 0);

INSERT INTO daily_log (id, apartment_id, log_date, total_litres_consumed, guest_count, day_cost)
VALUES (3, 3, CURRENT_DATE, 3000, 0, 0);

INSERT INTO daily_log (id, apartment_id, log_date, total_litres_consumed, guest_count, day_cost)
VALUES (4, 4, CURRENT_DATE, 300, 0, 0);