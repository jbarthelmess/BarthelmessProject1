create table users (
    user_id int primary key generated always as identity,
    username varchar(256) not null unique,
    pass_word varchar(256) not null, -- ought to be a hash of it or something, no passwords stored directly in the db
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
    status varchar(8) default 'PENDING',
    file_url varchar(3000)
);

alter table expense add foreign key (user_id) references users(user_id);
alter table expense add foreign key (manager_handler) references users(user_id);
alter table expense alter column status set not null;
alter table expense add constraint amount_cents_positive check (amount_cents>0);

insert into users (username, pass_word, is_manager) values ('TEST_USER_1', 'password', true);
insert into users (username, pass_word, is_manager) values ('TEST_USER_2', 'password', false);
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 3500, 'Office Party', 1614467456, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 40000, 'Gas Money for Road Trip', 1614467466, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 8000, 'Lunch on the road', 1614467476, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 7500, 'Cause I want Money', 1614467486, 'PENDING');

insert into users (username, pass_word, is_manager) values ('TEST_USER_1', 'password', true);
insert into users (username, pass_word, is_manager) values ('TEST_USER_2', 'password', false);
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 3500, 'Office Party', 1614467456, 'pending');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 40000, 'Gas Money for Road Trip', 1614467466, 'pending');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 8000, 'Lunch on the road', 1614467476, 'pending');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 7500, 'Cause I want Money', 1614467486, 'pending');