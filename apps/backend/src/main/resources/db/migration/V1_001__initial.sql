create table trader
(
    username text not null
        constraint users_pk
            primary key,
    password text not null,
    name     text not null
);

create table stock
(
    id   serial
        constraint stock_pk
            primary key,
    name text   not null
);

create table sell_order
(
    id              serial
        constraint sell_order_pk
            primary key,
    stock_id        integer            not null
        constraint sell_order_stock_id_fk
            references stock (id),
    quantity        integer            not null,
    price_per_share decimal            not null,
    trader          text               not null
        constraint sell_order_trader_id_fk
            references trader,
    cancelled       bool default false not null
);

create table buy_order
(
    id            serial
        constraint buy_order_pk
            primary key,
    sell_order_id integer not null
        constraint buy_order_sell_order_id_fk
            references sell_order,
    trader        text    not null
        constraint buy_order_trader_username_fk
            references trader,
    quantity      integer not null
);

create table wallet_transaction
(
    id     serial
        constraint wallet_transaction_pk
            primary key,
    trader text          not null
        constraint wallet_transaction_trader_username_fk
            references trader,
    amount numeric(16,2) not null,
    hidden boolean       not null
);

create table stock_holding
(
    trader   text    not null
        constraint stock_holding_trader_username_fk
            references trader,
    stock    integer not null
        constraint stock_holding_stock_id_fk
            references stock,
    quantity integer not null,
    constraint stock_holding_pk
        primary key (trader, stock)
);


