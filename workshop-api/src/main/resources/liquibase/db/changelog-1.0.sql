--liquibase formatted sql

--changeset ali.moro:1
CREATE TABLE IF NOT EXISTS public.users (
    id CHARACTER VARYING NOT NULL,
    first_name CHARACTER VARYING NOT NULL,
    last_name CHARACTER VARYING NOT NULL,
    CONSTRAINT user_id PRIMARY KEY (id)
);
