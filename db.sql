DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_token;
DROP TABLE IF EXISTS model;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS execution;


CREATE TABLE users
(
    id                 uuid PRIMARY KEY,
    creation_timestamp timestamp    NOT NULL,
    display_name       varchar(100) NULL,
    email              varchar(100) NOT NULL UNIQUE,
    hashed_password    varchar(255) NOT NULL,
    username           varchar(50)  NOT NULL UNIQUE,
    category           varchar(50)  NOT NULL
);

CREATE TABLE user_token
(
    user_id            uuid PRIMARY KEY,
    auth_token         varchar(255) NOT NULL,
    creation_timestamp timestamp    NOT NULL,
    expiry_timestamp   timestamp    NOT NULL
);

CREATE TABLE orders
(
    id               uuid PRIMARY KEY,
    user_id          uuid         NOT NULL REFERENCES users (id),
    trade_date       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status           varchar(255) NOT NULL,
    due_date         TIMESTAMP    NOT NULL,
    delivery_address VARCHAR(255) NOT NULL
);

CREATE TABLE model
(
    id          uuid PRIMARY KEY,
    name        VARCHAR NOT NULL,
    description VARCHAR(5000),
    type        VARCHAR(50),
    settings    VARCHAR(5000),
    material    VARCHAR(50)
);

CREATE TABLE order_details
(
    id        uuid PRIMARY KEY,
    order_id  uuid        NOT NULL REFERENCES orders (id),
    model_id  uuid        NOT NULL REFERENCES model (id),
    amount    INTEGER     NOT NULL,
    available INTEGER     NOT NULL,
    status    VARCHAR(50) NOT NULL,
    progress  VARCHAR(50) NOT NULL
);

CREATE TABLE execution
(
    id         uuid PRIMARY KEY,
    worker_id  uuid        NOT NULL REFERENCES users (id),
    order_id   uuid        NOT NULL REFERENCES orders (id),
    model_id   uuid        NOT NULL REFERENCES model (id),
    start_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total      INTEGER     NOT NULL,
    completed  INTEGER     NOT NULL,
    status     VARCHAR(50) NOT NULL,
    progress   VARCHAR(50) NOT NULL
);

