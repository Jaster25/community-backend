create table user
(
    user_id    uuid         not null,
    created_at timestamp    not null,
    password   varchar(255) not null,
    updated_at timestamp,
    username   varchar(255) not null,
    primary key (user_id)
);

create table user_roles
(
    user_id uuid not null,
    roles   varchar(255)
);

create table post
(
    post_id    bigint generated by default as identity,
    created_at timestamp    not null,
    content    clob         not null,
    title      varchar(255) not null,
    updated_at timestamp,
    view_count integer,
    user_id    uuid,
    primary key (post_id)
);