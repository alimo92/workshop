--liquibase formatted sql

--changeset ali.moro:1
ALTER TABLE public.users
ADD COLUMN balance INTEGER NOT NULL DEFAULT 0;
