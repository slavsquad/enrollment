CREATE TABLE item
(
    id uuid NOT NULL PRIMARY KEY UNIQUE,
    type character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    parent_id uuid,
    price integer NOT NULL,
    update_date timestamp without time zone
);