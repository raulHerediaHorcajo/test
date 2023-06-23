CREATE TABLE generator_type
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_generatortype PRIMARY KEY (id)
);

ALTER TABLE generator_type
    ADD CONSTRAINT uc_generatortype_name UNIQUE (name);