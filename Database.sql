DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
                       username       VARCHAR(255)    NOT NULL PRIMARY KEY,
                       password       VARCHAR(255)    NOT NULL,
                       coins          INTEGER NOT NULL,
                       elo            INTEGER NOT NULL,
                       wins           INTEGER NOT NULL,
                       losses         INTEGER NOT NULL,
                       bio            VARCHAR(255),
                       image          VARCHAR(255),
                       name           VARCHAR(255),
                       mtcg_token     VARCHAR(255)
);

DROP TABLE IF EXISTS cards CASCADE;
CREATE TABLE cards (
                       name           VARCHAR(255),
                       type           VARCHAR(255),
                       id             VARCHAR(255)    NOT NULL PRIMARY KEY,
                       damage         INTEGER,
                       element_type   VARCHAR(255),
                       package_id     VARCHAR(255),
                       buyable        BOOLEAN DEFAULT TRUE NOT NULL,
                       created_number INTEGER GENERATED ALWAYS AS IDENTITY (MINVALUE 0)
);

DROP TABLE IF EXISTS decks CASCADE;
CREATE TABLE decks (
                       owner_id       VARCHAR(255) NOT NULL PRIMARY KEY REFERENCES users ON DELETE CASCADE,
                       first_card_id  VARCHAR(255) REFERENCES cards ON DELETE CASCADE,
                       second_card_id VARCHAR(255) REFERENCES cards ON DELETE CASCADE,
                       third_card_id  VARCHAR(255) REFERENCES cards ON DELETE CASCADE,
                       fourth_card_id VARCHAR(255) REFERENCES cards ON DELETE CASCADE
);

DROP TABLE IF EXISTS stack CASCADE;
CREATE TABLE stack (
                       username VARCHAR(255) REFERENCES users ON DELETE CASCADE,
                       card_id  VARCHAR(255) REFERENCES cards ON DELETE CASCADE
);

DROP TABLE IF EXISTS customCard CASCADE;
CREATE TABLE customCard (
                       name           VARCHAR(255),
                       type           VARCHAR(255),
                       id             VARCHAR(255)    NOT NULL PRIMARY KEY,
                       damage         INTEGER,
                       element_type   VARCHAR(255),
                       package_id     VARCHAR(255)

);

