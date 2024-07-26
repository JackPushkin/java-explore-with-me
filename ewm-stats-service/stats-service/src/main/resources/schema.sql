DROP TABLE IF EXISTS endpoint_hits;

CREATE TABLE IF NOT EXISTS endpoint_hits (
    id_hit INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    service_identifier VARCHAR(64) NOT NULL,
    endpoint_uri VARCHAR(255) NOT NULL,
    client_ip VARCHAR(64) NOT NULL,
    request_date TIMESTAMP NOT NULL
)