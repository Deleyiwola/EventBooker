drop table if exists users;

drop table if exists event;

drop table if exists booking;

create table users (
                       id bigint not null auto_increment,
                       email varchar(255),
                       name varchar(255),
                       phone_number varchar(255),
                       primary key (id)
) engine=InnoDB;

create table event (
                       id bigint not null auto_increment,
                       event_name varchar(255),
                       capacity integer,
                       version integer,
                       start_time datetime(6),
                       end_time datetime(6),
                       primary key (id)
) engine=InnoDB;

create table booking (
                         booking_id bigint not null auto_increment,
                         number_of_seats_booked integer,
                         last_updated_time datetime(6),
                         time_booked datetime(6),
                         user_id bigint,
                         event_id bigint,
                         primary key (booking_id)
) engine=InnoDB;

drop table if exists users_seq;

alter table booking
    add constraint fk_booking_event
        foreign key (event_id)
            references event (id);

alter table booking
    add constraint fk_booking_user
        foreign key (user_id)
            references users (id);