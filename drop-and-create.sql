
    set client_min_messages = WARNING;

    alter table if exists booking 
       drop constraint if exists FKiy2tdi4vrw2mljj6rqwmd698q;

    alter table if exists booking 
       drop constraint if exists FK7udbel7q86k041591kj6lfmvw;

    drop table if exists booking cascade;

    drop table if exists event cascade;

    drop table if exists users cascade;

    drop sequence if exists users_seq;

    create sequence users_seq start with 1 increment by 50;

    create table booking (
        number_of_seats_booked integer,
        last_updated_time timestamp(6),
        time_booked timestamp(6),
        user_id bigint,
        booking_id uuid not null,
        event_id uuid,
        primary key (booking_id)
    );

    create table event (
        capacity integer,
        version integer,
        end_time timestamp(6),
        start_time timestamp(6),
        id uuid not null,
        event_name varchar(255),
        primary key (id)
    );

    create table users (
        id bigint not null,
        email varchar(255),
        name varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    alter table if exists booking 
       add constraint FKiy2tdi4vrw2mljj6rqwmd698q 
       foreign key (event_id) 
       references event;

    alter table if exists booking 
       add constraint FK7udbel7q86k041591kj6lfmvw 
       foreign key (user_id) 
       references users;

    set client_min_messages = WARNING;

    alter table if exists booking 
       drop constraint if exists FKiy2tdi4vrw2mljj6rqwmd698q;

    alter table if exists booking 
       drop constraint if exists FK7udbel7q86k041591kj6lfmvw;

    drop table if exists booking cascade;

    drop table if exists event cascade;

    drop table if exists users cascade;

    drop sequence if exists users_seq;

    create sequence users_seq start with 1 increment by 50;

    create table booking (
        number_of_seats_booked integer,
        last_updated_time timestamp(6),
        time_booked timestamp(6),
        user_id bigint,
        booking_id uuid not null,
        event_id uuid,
        primary key (booking_id)
    );

    create table event (
        capacity integer,
        version integer,
        end_time timestamp(6),
        start_time timestamp(6),
        id uuid not null,
        event_name varchar(255),
        primary key (id)
    );

    create table users (
        id bigint not null,
        email varchar(255),
        name varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    alter table if exists booking 
       add constraint FKiy2tdi4vrw2mljj6rqwmd698q 
       foreign key (event_id) 
       references event;

    alter table if exists booking 
       add constraint FK7udbel7q86k041591kj6lfmvw 
       foreign key (user_id) 
       references users;

    set client_min_messages = WARNING;

    alter table if exists booking 
       drop constraint if exists FKiy2tdi4vrw2mljj6rqwmd698q;

    alter table if exists booking 
       drop constraint if exists FK7udbel7q86k041591kj6lfmvw;

    drop table if exists booking cascade;

    drop table if exists event cascade;

    drop table if exists users cascade;

    drop sequence if exists users_seq;

    create sequence users_seq start with 1 increment by 50;

    create table booking (
        number_of_seats_booked integer,
        last_updated_time timestamp(6),
        time_booked timestamp(6),
        user_id bigint,
        booking_id uuid not null,
        event_id uuid,
        primary key (booking_id)
    );

    create table event (
        capacity integer,
        version integer,
        end_time timestamp(6),
        start_time timestamp(6),
        id uuid not null,
        event_name varchar(255),
        primary key (id)
    );

    create table users (
        id bigint not null,
        email varchar(255),
        name varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    alter table if exists booking 
       add constraint FKiy2tdi4vrw2mljj6rqwmd698q 
       foreign key (event_id) 
       references event;

    alter table if exists booking 
       add constraint FK7udbel7q86k041591kj6lfmvw 
       foreign key (user_id) 
       references users;

    set client_min_messages = WARNING;

    alter table if exists public.booking 
       drop constraint if exists FKiy2tdi4vrw2mljj6rqwmd698q;

    alter table if exists public.booking 
       drop constraint if exists FK7udbel7q86k041591kj6lfmvw;

    drop table if exists public.booking cascade;

    drop table if exists public.event cascade;

    drop table if exists public.users cascade;

    drop sequence if exists public.users_seq;

    create sequence public.users_seq start with 1 increment by 50;

    create table public.booking (
        number_of_seats_booked integer,
        last_updated_time timestamp(6),
        time_booked timestamp(6),
        user_id bigint,
        booking_id uuid not null,
        event_id uuid,
        primary key (booking_id)
    );

    create table public.event (
        capacity integer,
        version integer,
        end_time timestamp(6),
        start_time timestamp(6),
        id uuid not null,
        event_name varchar(255),
        primary key (id)
    );

    create table public.users (
        id bigint not null,
        email varchar(255),
        name varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    alter table if exists public.booking 
       add constraint FKiy2tdi4vrw2mljj6rqwmd698q 
       foreign key (event_id) 
       references public.event;

    alter table if exists public.booking 
       add constraint FK7udbel7q86k041591kj6lfmvw 
       foreign key (user_id) 
       references public.users;

    set client_min_messages = WARNING;

    alter table if exists public.booking 
       drop constraint if exists FKiy2tdi4vrw2mljj6rqwmd698q;

    alter table if exists public.booking 
       drop constraint if exists FK7udbel7q86k041591kj6lfmvw;

    drop table if exists public.booking cascade;

    drop table if exists public.event cascade;

    drop table if exists public.users cascade;

    drop sequence if exists public.users_seq;

    create sequence public.users_seq start with 1 increment by 50;

    create table public.booking (
        number_of_seats_booked integer,
        last_updated_time timestamp(6),
        time_booked timestamp(6),
        user_id bigint,
        booking_id uuid not null,
        event_id uuid,
        primary key (booking_id)
    );

    create table public.event (
        capacity integer,
        version integer,
        end_time timestamp(6),
        start_time timestamp(6),
        id uuid not null,
        event_name varchar(255),
        primary key (id)
    );

    create table public.users (
        id bigint not null,
        email varchar(255),
        name varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    alter table if exists public.booking 
       add constraint FKiy2tdi4vrw2mljj6rqwmd698q 
       foreign key (event_id) 
       references public.event;

    alter table if exists public.booking 
       add constraint FK7udbel7q86k041591kj6lfmvw 
       foreign key (user_id) 
       references public.users;
