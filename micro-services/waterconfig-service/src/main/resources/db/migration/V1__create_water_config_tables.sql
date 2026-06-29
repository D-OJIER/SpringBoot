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
    CONSTRAINT fk_asc_source FOREIGN KEY (source_id) REFERENCES water_source(id)
);
