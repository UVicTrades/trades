create table trader
(
    username text not null
        constraint users_pk
            primary key,
    password text not null,
    name     text not null
);
