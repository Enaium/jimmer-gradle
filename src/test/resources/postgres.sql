create table people
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    phone         varchar(11) unique not null,
    password      varchar(255)       not null
);

create table profile
(
    created_time  timestamp                          not null,
    modified_time timestamp                          not null,
    deleted       bool default false                 not null,
    id            uuid primary key,
    people_id     uuid unique references people (id) not null,
    nickname      varchar(50) unique                 not null,
    email         varchar(50) unique                 not null
);

create table topic
(
    created_time  timestamp                   not null,
    modified_time timestamp                   not null,
    deleted       bool default false          not null,
    id            uuid primary key,
    title         varchar(50) unique          not null,
    people_id     uuid references people (id) not null
);

create table question
(
    created_time  timestamp                   not null,
    modified_time timestamp                   not null,
    deleted       bool default false          not null,
    id            uuid primary key,
    title         varchar(50) unique          not null,
    content       text                        not null,
    people_id     uuid references people (id) not null
);

create table answer
(
    created_time  timestamp                     not null,
    modified_time timestamp                     not null,
    deleted       bool default false            not null,
    id            uuid primary key,
    content       text                          not null,
    people_id     uuid references people (id)   not null,
    question_id   uuid references question (id) not null
);

create table question_topic
(

    question_id uuid references question (id) not null,
    topic_id    uuid references topic (id)    not null
);

create table post
(
    created_time  timestamp                   not null,
    modified_time timestamp                   not null,
    deleted       bool default false          not null,
    id            uuid primary key,
    title         varchar(50)                 not null,
    content       text                        not null,
    people_id     uuid references people (id) not null
);

create table post_topic
(
    post_id  uuid references post (id)  not null,
    topic_id uuid references topic (id) not null
);

create table comment
(
    created_time  timestamp                   not null,
    modified_time timestamp                   not null,
    deleted       bool default false          not null,
    id            uuid primary key,
    content       text                        not null,
    people_id     uuid references people (id) not null,
    comment_id    uuid references comment (id),
    answer_id     uuid references answer (id),
    post_id       uuid references post (id)
);