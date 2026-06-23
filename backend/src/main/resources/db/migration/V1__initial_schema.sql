CREATE TABLE block (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE apartment_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    base_occupancy INT NOT NULL,
    litres_per_person DOUBLE NOT NULL
);

CREATE TABLE apartment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    block_id BIGINT,
    type_id BIGINT,
    CONSTRAINT fk_apartment_block FOREIGN KEY (block_id) REFERENCES block(id),
    CONSTRAINT fk_apartment_type FOREIGN KEY (type_id) REFERENCES apartment_type(id)
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    apartment_id BIGINT,
    CONSTRAINT fk_users_apartment FOREIGN KEY (apartment_id) REFERENCES apartment(id)
);

CREATE TABLE water_source (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    pricing_type VARCHAR(255) NOT NULL,
    supply_type VARCHAR(255) NOT NULL
);

CREATE TABLE water_rate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    min_litres DOUBLE NOT NULL,
    max_litres DOUBLE NOT NULL,
    rate_per_litre DOUBLE NOT NULL,
    effective_from DATE,
    effective_to DATE,
    source_id BIGINT,
    CONSTRAINT fk_water_rate_source FOREIGN KEY (source_id) REFERENCES water_source(id)
);

CREATE TABLE apartment_source_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ratio_percent DOUBLE NOT NULL,
    apartment_id BIGINT,
    source_id BIGINT,
    CONSTRAINT fk_asc_apartment FOREIGN KEY (apartment_id) REFERENCES apartment(id),
    CONSTRAINT fk_asc_source FOREIGN KEY (source_id) REFERENCES water_source(id)
);

CREATE TABLE daily_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_date DATE,
    total_litres_consumed DOUBLE NOT NULL,
    guest_count INT NOT NULL,
    day_cost DOUBLE NOT NULL,
    apartment_id BIGINT,
    CONSTRAINT fk_daily_log_apartment FOREIGN KEY (apartment_id) REFERENCES apartment(id)
);

CREATE INDEX idx_daily_log_date ON daily_log(log_date);
CREATE INDEX idx_daily_log_apartment_date ON daily_log(apartment_id, log_date);

CREATE TABLE daily_log_source_breakdown (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    litres DOUBLE NOT NULL,
    cost DOUBLE NOT NULL,
    daily_log_id BIGINT,
    source_id BIGINT,
    CONSTRAINT fk_dlsb_daily_log FOREIGN KEY (daily_log_id) REFERENCES daily_log(id),
    CONSTRAINT fk_dlsb_source FOREIGN KEY (source_id) REFERENCES water_source(id)
);

CREATE TABLE slab_monthly_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL,
    month INT NOT NULL,
    total_litres DOUBLE NOT NULL,
    total_cost DOUBLE NOT NULL,
    apartment_id BIGINT,
    source_id BIGINT,
    CONSTRAINT fk_sms_apartment FOREIGN KEY (apartment_id) REFERENCES apartment(id),
    CONSTRAINT fk_sms_source FOREIGN KEY (source_id) REFERENCES water_source(id)
);
