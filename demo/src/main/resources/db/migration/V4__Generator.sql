CREATE TABLE generator
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    name                VARCHAR(255) NOT NULL,
    society_id          BIGINT       NOT NULL,
    generator_type_id   BIGINT       NOT NULL,
    active              BOOLEAN       NOT NULL,
    initialization_date date         NOT NULL,
    termination_date    date NULL,
    periodicity         INT          NOT NULL,
    pickup_day          VARCHAR(255) NULL,
    off_day             VARCHAR(255) NULL,
    opening_time        time NULL,
    closing_time        time NULL,
    email               VARCHAR(255) NOT NULL,
    address             VARCHAR(255) NOT NULL,
    observations        VARCHAR(255) NULL,
    CONSTRAINT pk_generator PRIMARY KEY (id)
);

CREATE TABLE generator_phone_number
(
    generator_id BIGINT       NOT NULL,
    phone_number VARCHAR(255) NOT NULL
);

ALTER TABLE generator
    ADD CONSTRAINT uc_generator_name UNIQUE (name);

ALTER TABLE generator
    ADD CONSTRAINT uc_generator_address UNIQUE (address);

ALTER TABLE generator
    ADD CONSTRAINT fk_generator_on_generatortype FOREIGN KEY (generator_type_id) REFERENCES generator_type (id);

ALTER TABLE generator
    ADD CONSTRAINT fk_generator_on_society FOREIGN KEY (society_id) REFERENCES society (id);

ALTER TABLE generator_phone_number
    ADD CONSTRAINT fk_generator_phonenumber_on_generator FOREIGN KEY (generator_id) REFERENCES generator (id);