INSERT INTO user (ID, NAME, EMAIL, PASSWORD)
VALUES (1, 'admin', 'admin@gmail.com', '$2a$10$gDc4SqW9Y9VsPNDV63krR.yNGhVkVBdRFUU9GUV6VhuSfi6neNr8K');

INSERT INTO user_roles (USER_ID, ROLES)
VALUES (1, 'USER');

INSERT INTO user_roles (USER_ID, ROLES)
VALUES (1, 'ADMIN');

INSERT INTO user (ID, NAME, EMAIL, PASSWORD)
VALUES (2, 'user', 'user@gmail.com', '$2a$10$1ok3CeCSVd/GyiguPQwAS.Nw3tvOoBcX0n4ZCn9wV5mpFy3Z74Z2.');

INSERT INTO user_roles (USER_ID, ROLES)
VALUES (2, 'USER');