CREATE TABLE daily_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_date DATE,
    total_litres_consumed DOUBLE NOT NULL,
    guest_count INT NOT NULL,
    day_cost DOUBLE NOT NULL,
    apartment_id BIGINT,
    apartment_number VARCHAR(255)
);

CREATE INDEX idx_daily_log_date ON daily_log(log_date);
CREATE INDEX idx_daily_log_apartment_date ON daily_log(apartment_id, log_date);

CREATE TABLE daily_log_source_breakdown (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    litres DOUBLE NOT NULL,
    cost DOUBLE NOT NULL,
    daily_log_id BIGINT,
    source_id BIGINT,
    CONSTRAINT fk_dlsb_daily_log FOREIGN KEY (daily_log_id) REFERENCES daily_log(id)
);

CREATE TABLE slab_monthly_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL,
    month INT NOT NULL,
    total_litres DOUBLE NOT NULL,
    total_cost DOUBLE NOT NULL,
    apartment_id BIGINT,
    source_id BIGINT
);
