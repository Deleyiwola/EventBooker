
alter table booking
drop
foreign key FKiy2tdi4vrw2mljj6rqwmd698q;

alter table booking
drop
foreign key FK7udbel7q86k041591kj6lfmvw;

drop table if exists booking;

drop table if exists event;

drop table if exists users;

drop table if exists users_seq;

create table booking (
                         number_of_seats_booked integer,
                         last_updated_time datetime(6),
                         time_booked datetime(6),
                         user_id bigint,
                         booking_id binary(16) not null,
                         event_id binary(16),
                         primary key (booking_id)
) engine=InnoDB;

create table event (
                       capacity integer,
                       version integer,
                       end_time datetime(6),
                       start_time datetime(6),
                       id binary(16) not null,
                       event_name varchar(255),
                       primary key (id)
) engine=InnoDB;

create table users (
                       id bigint not null,
                       email varchar(255),
                       name varchar(255),
                       phone_number varchar(255),
                       primary key (id)
) engine=InnoDB;

create table users_seq (
                           next_val bigint
) engine=InnoDB;

insert into users_seq ( next_val ) values ( 1 );

alter table booking
    add constraint FKiy2tdi4vrw2mljj6rqwmd698q
        foreign key (event_id)
            references event (id);

alter table booking
    add constraint FK7udbel7q86k041591kj6lfmvw
        foreign key (user_id)
            references users (id);

alter table booking
drop
foreign key FKiy2tdi4vrw2mljj6rqwmd698q;

alter table booking
drop
foreign key FK7udbel7q86k041591kj6lfmvw;

drop table if exists booking;

drop table if exists event;

drop table if exists users;

drop table if exists users_seq;

create table booking (
                         number_of_seats_booked integer,
                         last_updated_time datetime(6),
                         time_booked datetime(6),
                         user_id bigint,
                         booking_id binary(16) not null,
                         event_id binary(16),
                         primary key (booking_id)
) engine=InnoDB;

create table event (
                       capacity integer,
                       version integer,
                       end_time datetime(6),
                       start_time datetime(6),
                       id binary(16) not null,
                       event_name varchar(255),
                       primary key (id)
) engine=InnoDB;

create table users (
                       id bigint not null,
                       email varchar(255),
                       name varchar(255),
                       phone_number varchar(255),
                       primary key (id)
) engine=InnoDB;

create table users_seq (
                           next_val bigint
) engine=InnoDB;

insert into users_seq ( next_val ) values ( 1 );

alter table booking
    add constraint FKiy2tdi4vrw2mljj6rqwmd698q
        foreign key (event_id)
            references event (id);

alter table booking
    add constraint FK7udbel7q86k041591kj6lfmvw
        foreign key (user_id)
            references users (id);
