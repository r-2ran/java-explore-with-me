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
  annotation varchar not null,
  description varchar not null,
  title varchar not null,
  state varchar not null,
  initiator_id bigint not null references users(id) on delete cascade,
  category_id bigint not null references categories(id) on delete cascade,
  location_id bigint not null references locations(id) on delete cascade,
  created timestamp without time zone not null,
  event_date timestamp without time zone not null,
  published_on timestamp without time zone,
  paid boolean,
  request_moderation boolean,
  participant_limit int default 0,
  views int not null
);

create table if not exists requests
(
  id bigint generated by default as identity primary key,
    requester_id bigint references users (id) on delete cascade,
    event_id bigint references events (id) on delete cascade,
    created timestamp not null,
    status varchar not null
);

create table if not exists compilations
(
  id bigint generated by default as identity primary key,
  pinned boolean default false,
  title  varchar not null
);

create table if not exists compilations_events
(
  compilation_id bigint references compilations (id) on delete cascade,
  event_id bigint references events (id) on delete cascade,
  primary key (compilation_id, event_id)
);