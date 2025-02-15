CREATE TYPE status AS ENUM ('IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE orderType AS ENUM ('MARKET', 'LIMIT');

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

-- stock transactions are created when an order has been placed in the matching engine
create table stock_order (
    stock_tx_id serial not null
         constraint stock_tx_pk
             primary key,
    parent_stock_tx_id integer, -- References the id of a tx that is selling to
                -- wallet_tx_id integer references wallet_tx(wallet_tx_id) not null, -- causes a circular dependency, so do not include
    order_status status not null,
    order_type orderType not null,
    stock_id integer references stock(stock_id) not null,
    is_buy boolean not null,
    quantity integer not null,
    stock_price decimal(10, 2) not null,
    time_stamp timestamp default current_timestamp,
    owner text references trader(username) not null -- if LIMIT, this is the seller. if MARKET, this is the buyer
);


-- wallet transactions are only made when a stock transaction has completed
create table wallet_tx
(
    wallet_tx_id serial not null
        constraint wallet_tx_pk
            primary key,
    username text references trader(username) not null,
    stock_tx_id integer references stock_order(stock_tx_id) not null,
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