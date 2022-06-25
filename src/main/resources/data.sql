-- user1
insert
into user (created_at, password, updated_at, username, user_id)
values ('2021-12-21T12:35:31.406332',
        '{bcrypt}$2a$10$sak6HBjaDTzojNAqoyWN5uk/h6futsWbUUSYOttdkATBlTzbYyj6O',
        null,
        'user1',
        'b5368610-f48d-49a2-945c-74fc17890b14');
-- user2
insert into user(created_at, password, updated_at, username, user_id)
values ('2022-06-23T20:53:34.336388',
        '{bcrypt}$2a$10$7GDT5AxvRScIUaFNHXtwre12o.WCzjfNyDnggDHAlXiV1tZr.IEYS',
        null,
        'user2',
        '81a2c6ae-c324-4379-9618-858ff856c1e1');
-- admin1
insert
into user (created_at, password, updated_at, username, user_id)
values ('2020-03-10T03:52:44.600374',
        '{bcrypt}$2a$10$bkyXPgVXuRXmRa9d34FI2OUAteBNZi0BN8EYkG.loOSK/D2CR9jYO',
        null,
        'admin1',
        'b7b35ca2-93b6-4ae9-b505-cc32fc1674a9');

-- user1
insert
into user_roles (user_id, roles)
values ('b5368610-f48d-49a2-945c-74fc17890b14', 'ROLE_USER');
-- user2
insert
into user_roles (user_id, roles)
values ('81a2c6ae-c324-4379-9618-858ff856c1e1', 'ROLE_USER');
-- admin1
insert
into user_roles (user_id, roles)
values ('b7b35ca2-93b6-4ae9-b505-cc32fc1674a9', 'ROLE_USER');
insert
into user_roles (user_id, roles)
values ('b7b35ca2-93b6-4ae9-b505-cc32fc1674a9', 'ROLE_ADMIN');