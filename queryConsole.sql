DROP TABLE databasechangelog;
DROP TABLE databasechangeloglock;
DROP TABLE task;

CREATE TABLE task
(
    id          bigserial primary key,
    title       varchar(255),
    description varchar(255),
    status      varchar(50),
    createdAt   TIMESTAMP,
    updatedAt   TIMESTAMP
);

DELETE FROM task WHERE id = 1