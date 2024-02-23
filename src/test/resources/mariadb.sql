create table people
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    phone         varchar(11) unique not null,
    password      varchar(255)       not null
);

create table topic
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    title         varchar(50) unique not null,
    people_id     uuid               not null references people (id)
);

create table question
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    title         varchar(50) unique not null,
    content       text               not null,
    people_id     uuid               not null references people (id)
);

create table answer
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    content       text               not null,
    people_id     uuid               not null references people (id),
    question_id   uuid               not null references question (id)
);

create table question_topic
(
    question_id uuid not null references question (id),
    topic_id    uuid not null references topic (id),
    primary key (question_id, topic_id)
);

create table post
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    title         varchar(50)        not null,
    content       text               not null,
    people_id     uuid               not null references people (id)
);

create table post_topic
(
    post_id  uuid not null references post (id),
    topic_id uuid not null references topic (id),
    primary key (post_id, topic_id)
);

create table comment
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            uuid primary key,
    content       text               not null,
    people_id     uuid               not null references people (id),
    comment_id    uuid references comment (id),
    answer_id     uuid references answer (id),
    post_id       uuid references post (id)
);