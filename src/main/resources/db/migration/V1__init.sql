CREATE TABLE node
(
    id uuid NOT NULL PRIMARY KEY UNIQUE,
    type character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    parent_id uuid,
    price integer NOT NULL,
    date timestamp with time zone
);