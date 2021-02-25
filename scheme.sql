create type resolve_status as enum ('pending', 'denied', 'approved');

create table user (
    user_id int primary key generated always as identity,
    username varchar(256) not null unique,
    password varchar(256) not null, -- ought to be a hash of it or something, no passwords stored directly in the db
    is_manager boolean default false
);

create table expense (
    expense_id int primary key generated always as identity,
    user_id int not null,
    manager_handler int,
    amount_cents int not null,
    reason_submitted varchar(3000) not null,
    reason_resolved varchar(3000) default null,
    dateSubmitted bigint not null,
    dateResolved bigint default null,
    status resolve_status not null
);

alter table expense add foreign key (user_id) references user(user_id);
alter table expense add foreign key (user_id) references user(user_id);