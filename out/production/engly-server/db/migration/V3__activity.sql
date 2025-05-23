create table if not exists activity_logs
(
    id         varchar(255)                not null
        primary key,
    action     varchar(255)
        constraint activity_logs_action_check
            check ((action)::text = ANY
                   ((ARRAY ['JOINED'::character varying, 'LEFT'::character varying, 'ROOM_CREATED'::character varying, 'SENT_MESSAGE'::character varying, 'DELETED_MESSAGE'::character varying, 'EDITED_MESSAGE'::character varying])::text[])),
    created_at timestamp(6) with time zone not null,
    user_id    varchar(255)
        constraint fk5bm1lt4f4eevt8lv2517soakd
            references users
);

create table if not exists notifications
(
    id         varchar(255)                not null
        primary key,
    content    varchar(255)                not null,
    created_at timestamp(6) with time zone not null,
    is_read    boolean,
    user_id    varchar(255)
        constraint fk9y21adhxn0ayjhfocscqox7bh
            references users
);

create table if not exists moderation
(
    id         varchar(255)                not null
        primary key,
    action     varchar(255)                not null
        constraint moderation_action_check
            check ((action)::text = ANY
                   ((ARRAY ['JOINED'::character varying, 'LEFT'::character varying, 'ROOM_CREATED'::character varying, 'SENT_MESSAGE'::character varying, 'DELETED_MESSAGE'::character varying, 'EDITED_MESSAGE'::character varying])::text[])),
    created_at timestamp(6) with time zone not null,
    reason     varchar(255)                not null,
    moder_id   varchar(255)
        constraint fkpnmnf9vl1cf5bw0e0ojuua0na
            references users,
    room_id    varchar(255)
        constraint fkcacq22ufv848yvsjylkdtpn2t
            references rooms,
    user_id    varchar(255)
        constraint ukcn70ck9ncvr4ggrn6lyot1ggx
            unique
        constraint fk2qlcsiln164lhsiaod0063opu
            references users
);

create table if not exists messages
(
    id         varchar(255)                not null
        primary key,
    content    varchar(255)                not null,
    created_at timestamp(6) with time zone not null,
    is_deleted boolean,
    is_edited  boolean,
    updated_at timestamp(6) with time zone,
    room_id    varchar(255)
        constraint fk9byh2oycnq4p3c76777tkjs6g
            references rooms,
    user_id    varchar(255)                not null
        constraint fkpdrb79dg3bgym7pydlf9k3p1n
            references users
);

create table if not exists chat_participants
(
    id        varchar(255)                not null
        primary key,
    joined_at timestamp(6) with time zone not null,
    leave_at  timestamp(6) with time zone,
    role      varchar(255)
        constraint chat_participants_role_check
            check ((role)::text = ANY
                   ((ARRAY ['ROLE_ADMIN'::character varying, 'ROLE_MANAGER'::character varying, 'ROLE_USER'::character varying, 'ROLE_GOOGLE'::character varying, 'ROLE_SYSADMIN'::character varying, 'ROLE_NOT_VERIFIED'::character varying])::text[])),
    room_id   varchar(255)
        constraint fka8swjf2uxk3rlbqhufqo4wej2
            references rooms,
    user_id   varchar(255)
        constraint fkbhdyxo0ndtbs1t49l28y21rkw
            references users
);

