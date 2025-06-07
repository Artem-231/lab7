CREATE TABLE users (
                       login TEXT PRIMARY KEY,
                       password_hash TEXT NOT NULL
);

CREATE TABLE labworks (
                          id SERIAL PRIMARY KEY,
                          name TEXT NOT NULL,
                          x DOUBLE PRECISION NOT NULL,
                          y BIGINT NOT NULL,
                          creation_date DATE NOT NULL,
                          minimal_point BIGINT NOT NULL,
                          description TEXT NOT NULL,
                          difficulty TEXT,
                          owner_login TEXT NOT NULL REFERENCES users(login)
);
