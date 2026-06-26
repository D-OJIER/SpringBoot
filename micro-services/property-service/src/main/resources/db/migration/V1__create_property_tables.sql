-- property-service schema: water_property
-- Owns: block, apartment_type, and apartment tables

CREATE TABLE block (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE apartment_type (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    base_occupancy     INT          NOT NULL,
    litres_per_person  DOUBLE       NOT NULL
);

CREATE TABLE apartment (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    number   VARCHAR(255) NOT NULL,
    block_id BIGINT,
    type_id  BIGINT,
    CONSTRAINT fk_apartment_block FOREIGN KEY (block_id) REFERENCES block (id),
    CONSTRAINT fk_apartment_type FOREIGN KEY (type_id) REFERENCES apartment_type (id)
);
