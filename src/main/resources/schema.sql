CREATE TABLE users (
    id IDENTITY,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE matches (
    id IDENTITY,
    user1 INT NOT NULL,
    user2 INT NOT NULL,
    user1Hand VARCHAR NOT NULL,
    user2Hand VARCHAR NOT NULL,
    isActive BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);
CREATE TABLE matchinfo (
    id IDENTITY,
    user1 INT NOT NULL,
    user2 INT NOT NULL,
    user1Hand VARCHAR NOT NULL,
    isActive BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);
