CREATE TYPE status AS ENUM ('IN_PROGRESS', 'COMPLETED', 'CANCELLED');

create table trader
(
    username text not null
        constraint users_pk
            primary key,
    password text not null,
    name     text not null,
    balance decimal(10, 2) default 0 not null
);

create table stock
(
    stock_id serial not null
        constraint stock_pk
            primary key,
    stock_name text not null
);

create table stock_tx (
    stock_tx_id serial not null
        constraint stock_tx_pk
            primary key,
    parent_stock_tx_id integer, -- References the id of a tx that is selling to
    order_status status not null,
    stock_id integer references stock(stock_id) not null,
    is_debit boolean not null,
    amount decimal(10, 2) not null,
    time_stamp timestamp default current_timestamp
);

create table wallet_tx
(
    wallet_tx_id serial not null
        constraint wallet_tx_pk
            primary key,
    username text references trader(username) not null,
    stock_tx_id integer references stock_tx(stock_tx_id) not null,
    is_debit boolean not null,
    amount decimal(10, 2) not null,
    time_stamp timestamp default current_timestamp
);