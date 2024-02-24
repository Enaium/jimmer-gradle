create table people
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    phone         varchar(11) unique not null,
    password      varchar(255)       not null
);

create table profile
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    people_id     bigint unique      not null,
    foreign key (people_id) references people (id),
    nickname      varchar(50) unique not null,
    email         varchar(50) unique not null
);

create table topic
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    title         varchar(50) unique not null,
    people_id     bigint             not null,
    foreign key (people_id) references people (id)
);

create table question
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    title         varchar(50) unique not null,
    content       text               not null,
    people_id     bigint             not null,
    foreign key (people_id) references people (id)
);

create table answer
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    content       text               not null,
    people_id     bigint             not null,
    foreign key (people_id) references people (id),
    question_id   bigint             not null,
    foreign key (question_id) references question (id)
);

create table question_topic
(
    question_id bigint not null,
    foreign key (question_id) references question (id),
    topic_id    bigint not null references topic (id),
    foreign key (topic_id) references topic (id)
);

create table post
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    title         varchar(50)        not null,
    content       text               not null,
    people_id     bigint             not null,
    foreign key (people_id) references people (id)
);

create table post_topic
(
    post_id  bigint not null references post (id),
    foreign key (post_id) references question (id),
    topic_id bigint not null references topic (id),
    foreign key (topic_id) references topic (id)
);

create table comment
(
    created_time  timestamp          not null,
    modified_time timestamp          not null,
    deleted       bool default false not null,
    id            bigint primary key auto_increment,
    content       text               not null,
    people_id     bigint             not null,
    foreign key (people_id) references people (id),
    comment_id    bigint,
    foreign key (comment_id) references comment (id),
    answer_id     bigint,
    foreign key (answer_id) references answer (id),
    post_id       bigint,
    foreign key (post_id) references people (id)
);
