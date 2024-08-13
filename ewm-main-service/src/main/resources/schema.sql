DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS events_compilations CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  SERIAL PRIMARY KEY,
    lat NUMERIC(8, 6),
    lon NUMERIC(9, 6),
    UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id            SERIAL PRIMARY KEY,
    annotation    TEXT                        NOT NULL,
    title         TEXT                        NOT NULL,
    description   TEXT                        NOT NULL,
    id_category   INTEGER                     NOT NULL,
    creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    publish_date  TIMESTAMP WITHOUT TIME ZONE,
    event_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id_initiator  INTEGER                     NOT NULL,
    id_location   INTEGER,
    paid          BOOLEAN                     NOT NULL,
    part_limit    INTEGER DEFAULT 0,
    moderation    BOOLEAN                     NOT NULL,
    state         VARCHAR(16)                 NOT NULL,
    view          INTEGER DEFAULT 0,
    FOREIGN KEY (id_initiator) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (id_location) REFERENCES locations (id),
    FOREIGN KEY (id_category) REFERENCES categories (id),
    CHECK (char_length(description) >= 20 AND char_length(description) <= 7000),
    CHECK (char_length(annotation) >= 20 AND char_length(annotation) <= 2000),
    CHECK (char_length(title) >= 3 AND char_length(title) <= 120)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     SERIAL PRIMARY KEY,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(64) NOT NULL,
    UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    id             SERIAL PRIMARY KEY,
    id_event       INTEGER NOT NULL,
    id_compilation INTEGER NOT NULL,
    UNIQUE (id_event, id_compilation),
    FOREIGN KEY (id_event) REFERENCES events (id),
    FOREIGN KEY (id_compilation) REFERENCES compilations (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           SERIAL PRIMARY KEY,
    id_event     INTEGER                     NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id_requester INTEGER                     NOT NULL,
    status       VARCHAR(16)                 NOT NULL,
    FOREIGN KEY (id_event) REFERENCES events (id),
    FOREIGN KEY (id_requester) REFERENCES users (id),
    UNIQUE (id_event, id_requester)
);
