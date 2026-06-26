-- auth-service schema: water_auth
-- Only owns: users table
-- Note: apartment_id is a plain FK reference to property-service's data.
-- There is intentionally NO foreign key constraint to the apartment table
-- because apartment is owned by property-service (separate schema/service).

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255)        NOT NULL,
    role        VARCHAR(50)         NOT NULL,
    apartment_id BIGINT             NULL
    -- No FK constraint: apartment_id references property-service, not a local table
);
