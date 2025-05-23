create table if not exists categories
(
    id          varchar(255)                not null
        primary key,
    created_at  timestamp(6) with time zone not null,
    description varchar(255)                not null,
    name        varchar(255)                not null
        constraint ukt8o6pivur7nn124jehx7cygw5
            unique
        constraint categories_name_check
            check ((name)::text = ANY
                   ((ARRAY ['HOBBIES'::character varying, 'TECH'::character varying, 'NEWS'::character varying, 'GENERAL_CHAT'::character varying, 'SPORTS'::character varying, 'TRAVEL_AND_FOOD'::character varying, 'CAREER'::character varying, 'MOVIES'::character varying])::text[])),
    updated_at  timestamp(6) with time zone
);

create table if not exists rooms
(
    id          varchar(255)                not null
        primary key,
    created_at  timestamp(6) with time zone not null,
    description varchar(255),
    name        varchar(255)                not null,
    updated_at  timestamp(6) with time zone,
    category_id varchar(255)
        constraint fkny0kelw84f5mokcnk9pga4qb9
            references categories,
    creator_id  varchar(255)
        constraint fk8s3s7efdr77enfswr3k332f5q
            references users
);

create table if not exists statistics
(
    id                varchar(255) not null
        primary key,
    last_message_time timestamp(6) not null,
    message_count     bigint       not null,
    room_id           varchar(255)
        constraint fkcg4xleo3ygoniq2rker691nje
            references rooms
);