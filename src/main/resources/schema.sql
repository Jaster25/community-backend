create table users
(
    user_id           varchar(255) not null,
    password          varchar(255) not null,
    username          varchar(255) not null,
    profile_image_url varchar(255),
    point             integer,
    created_at        timestamp,
    updated_at        timestamp,
    primary key (user_id)
);

create table user_roles
(
    user_id varchar(255) not null,
    roles   varchar(255)
);

create table post
(
    post_id    bigint AUTO_INCREMENT not null,
    user_id    varchar(255),
    title      varchar(255)          not null,
    content    clob                  not null, -- MySQL: clob -> longtext
    view_count integer,
    created_at timestamp,
    updated_at timestamp,
    primary key (post_id)
);

create table comment
(
    comment_id bigint AUTO_INCREMENT not null,
    post_id    bigint,
    user_id    varchar(255),
    parent_id  bigint,
    content    varchar(255)          not null,
    is_deleted boolean               not null,
    created_at timestamp,
    updated_at timestamp,
    primary key (comment_id)
);

create table like_post
(
    like_post_id bigint AUTO_INCREMENT not null,
    post_id      bigint,
    user_id      varchar(255),
    created_at   timestamp,
    primary key (like_post_id)
);

create table like_comment
(
    like_comment_id bigint AUTO_INCREMENT not null,
    comment_id      bigint,
    user_id         varchar(255),
    created_at      timestamp,
    primary key (like_comment_id)
);