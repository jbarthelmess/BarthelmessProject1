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

insert into users (username, pass_word, is_manager) values ('Andrew Wiggin', 'Ender', false);
insert into users (username, pass_word, is_manager) values ('Peter Wiggin', 'Locke', true);
insert into users (username, pass_word, is_manager) values ('Valentine Wiggin', 'Demosthenes', false);
insert into users (username, pass_word, is_manager) values ('Hyrum Graff', 'Colonel', true);
insert into users (username, pass_word, is_manager) values ('Mazer Rackham', 'General', true);
insert into users (username, pass_word, is_manager) values ('Anderson', 'Major', true);
insert into users (username, pass_word, is_manager) values ('Petra Arkanian', 'Salamander', false);
insert into users (username, pass_word, is_manager) values ('Julian Delphiki', 'Bean', false);


insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 2000, 'Unfair Battles', 1614467456, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 7000, 'All Green Recruits', 1614467466, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 30000, 'New Article', 1614467476, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (3, 25000, 'New Article', 1614467486, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (3, 10000, 'Call to Ender', 1614467949, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (4, 350000, 'Damages to Training Center', 1614467959, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (5, 400000000, 'Spaceship Time Travel', 1614467969, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (6, 100000, 'Psych trauma bill', 1614467979, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (7, 4500, 'Training new army', 1614467989, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (7, 2100, 'Teaching Ender to Shoot', 1614467999, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (8, 10000, 'Being Smarter than Ender', 1614468009, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (8, 5000, 'Because I am short', 1614468019, 'PENDING');

-- for Postman tests
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status, dateResolved, manager_handler) values (1, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'APPROVED', 1614467457, 4);
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (1, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'PENDING');

insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status, dateResolved, manager_handler) values (2, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'APPROVED', 1614467457, 4);
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'PENDING');
insert into expense (user_id, amount_cents, reason_submitted, dateSubmitted, status) values (2, 1, 'POSTMAN TEST EXPENSE', 1614467456, 'PENDING');