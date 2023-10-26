drop table if exists EVENTS, users, CATEGORIES, LOCATIONS, REQUESTS, COMPILATIONS_EVENTS, COMPILATIONS;

create table if not exists users
(
  id bigint generated by default as identity primary key,
  name  varchar not null,
  email varchar not null,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create table if not exists categories
(
  id bigint generated by default as identity primary key,
  name varchar not null,
  CONSTRAINT UQ_CAT_NAME UNIQUE (name)
);

create table if not exists locations
(
  id bigint generated by default as identity primary key,
  lat float not  null,
  lon float not null
);

create table if not exists events
(
  id bigint generated by default as identity primary key,
  annotation         varchar not null,
  category           bigint references categories (id),
  created_on         timestamp,
  description        text not null,
  event_date         timestamp,
  initiator          bigint references users (id) on delete cascade,
  location           bigint references locations (id) on delete cascade,
  paid               boolean,
  participant_limit  integer,
  published_on       timestamp,
  request_moderation boolean,
  state              varchar not null,
  title              varchar not null
);

create table if not exists requests
(
  id bigint generated by default as identity primary key,
    requester bigint references users (id) on delete cascade,
    event     bigint references events (id) on delete cascade,
    created   timestamp not null,
    status    varchar not null
);

create table if not exists compilations
(
  id bigint generated by default as identity primary key,
  pinned boolean,
  title  varchar not null
);

create table if not exists compilations_events
(
  compilation_id bigint references compilations (id) on delete cascade,
  event_id       bigint references events (id) on delete cascade,
  primary key (compilation_id, event_id)
);