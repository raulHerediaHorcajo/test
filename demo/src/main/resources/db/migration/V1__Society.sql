CREATE TABLE society
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    cif_dni VARCHAR(255) NULL,
    name    VARCHAR(255) NULL,
    CONSTRAINT pk_society PRIMARY KEY (id)
);

ALTER TABLE society
    ADD CONSTRAINT uc_society_cifdni UNIQUE (cif_dni);

ALTER TABLE society
    ADD CONSTRAINT uc_society_name UNIQUE (name);