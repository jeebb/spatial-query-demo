create extension if not exists postgis;

create table if not exists store_location(
    id bigserial NOT NULL CONSTRAINT store_location_pkey PRIMARY KEY,
    store_name varchar(255) not null,
    location_coordinates geometry(Point, 4326)
)