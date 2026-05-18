DROP DATABASE IF EXISTS restdb;

DROP ROLE IF EXISTS restadmin;

CREATE DATABASE restdb;

CREATE ROLE restadmin
    WITH
    LOGIN
    PASSWORD 'password';

GRANT ALL PRIVILEGES
    ON DATABASE restdb
    TO restadmin;