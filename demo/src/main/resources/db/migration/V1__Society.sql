CREATE TABLE society
(
    cif_dni VARCHAR(255) NOT NULL,
    name    VARCHAR(255) NULL,
    CONSTRAINT pk_society PRIMARY KEY (cif_dni)
);

ALTER TABLE society
    ADD CONSTRAINT uc_society_name UNIQUE (name);