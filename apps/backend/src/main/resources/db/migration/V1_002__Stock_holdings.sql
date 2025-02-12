create table stock_holdings
(
    stock_id        integer
        constraint stock_holdings_stock_stock_id_fk
            references stock,
    trader_username text
        constraint stock_holdings_trader_username_fk
            references trader,
    quantity        integer not null,
    constraint stock_holdings_pk
        primary key (stock_id, trader_username)
);
