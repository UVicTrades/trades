create table trader
(
    username text not null
        constraint users_pk
            primary key,
    password text not null,
    name     text not null,
    created_at timestamp default current_timestamp
);

create table wallet
(
    wallet_id serial not null
        contraint wallet_pk
            primary key,
    username text references trader(username) not null,
    balance decimal(10, 2) default 0 not null,
    modified_at timestamp default current_timestamp
);

create table wallet_tx
(
    wallet_tx_id serial not null
        constraint wallet_tx_pk
            primary key,
    wallet_id serial references wallet(wallet_id) not null,
    is_debit boolean not null,
    amount decimal(10, 2) not null
)

create table stock_tx
(
    stock_tx_id serial not null
        contraint stock_tx_pk
            primary key,
    stock_id not null, -- stocks are stored in memory, no reference in db
    is_debit boolean not null,
    amount decimal(10, 2) not null,
    time_stamp timestamp default current_timestamp

)

