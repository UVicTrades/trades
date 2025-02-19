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

create sequence shared_order_seq start with 1 increment by 1;

create table sell_order
(
    id              integer
        default nextval('shared_order_seq')
        constraint sell_order_pk
            primary key,
    stock_id        integer            not null
        constraint sell_order_stock_id_fk
            references stock (id),
    quantity        integer            not null,
    price_per_share decimal(16,2)      not null,
    trader          text               not null
        constraint sell_order_trader_id_fk
            references trader,
    cancelled       bool default false not null
);

create table buy_order
(
    id            integer
        default nextval('shared_order_seq')
        constraint buy_order_pk
            primary key,
    trader        text    not null
        constraint buy_order_trader_username_fk
            references trader,
    quantity      integer not null
);

create table buy_order_sell_order
(
    buy_order_id  integer not null
        constraint buy_order_sell_order_buy_order_id_fk
            references buy_order,
    sell_order_id integer not null
        constraint buy_order_sell_order_sell_order_id_fk
            references sell_order,
    quantity      integer not null,
    constraint buy_order_sell_order_pk
        primary key (buy_order_id, sell_order_id)
);

comment on column buy_order_sell_order.quantity is 'How many shares in the sell order this buy order takes.';



create table wallet_transaction
(
    id           serial
        constraint wallet_transaction_pk
            primary key,
    trader       text          not null
        constraint wallet_transaction_trader_username_fk
            references trader,
    amount       numeric(16,2) not null,
    buy_order_id integer
        constraint wallet_transaction_buy_order_id_fk
            references buy_order(id)
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


