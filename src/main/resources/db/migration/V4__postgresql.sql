CREATE TABLE organizer_request (
                                   id BIGSERIAL PRIMARY KEY,
                                   user_id BIGINT NOT NULL,
                                   organization_name VARCHAR(255),
                                   admin_comment VARCHAR(255),
                                   description VARCHAR(1000),
                                   status VARCHAR(50) NOT NULL,
                                   request_date TIMESTAMP,
                                   reviewed_date TIMESTAMP,

                                   CONSTRAINT fk_organizer_request_user
                                       FOREIGN KEY (user_id)
                                           REFERENCES users(id)
);