CREATE TABLE IF NOT EXISTS RESERVATION_TIME (
    ID       BIGINT       NOT NULL AUTO_INCREMENT,
    START_AT VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS THEME (
    ID          BIGINT       NOT NULL AUTO_INCREMENT,
    NAME        VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(255) NOT NULL,
    THUMBNAIL   VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS MEMBER (
    ID   BIGINT       NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255) NOT NULL,
    EMAIL VARCHAR(255) NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);
CREATE INDEX IF NOT EXISTS MEMBER_EMAIL ON MEMBER(EMAIL);

CREATE TABLE IF NOT EXISTS ROLE (
    MEMBER_ID BIGINT      NOT NULL,
    ROLE      VARCHAR(10) NOT NULL,
    PRIMARY KEY (MEMBER_ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (ID)
);

CREATE TABLE IF NOT EXISTS RESERVATION (
    ID   BIGINT       NOT NULL AUTO_INCREMENT,
    MEMBER_ID BIGINT,
    DATE VARCHAR(255) NOT NULL,
    TIME_ID BIGINT,
    THEME_ID BIGINT,
    PRIMARY KEY (ID),
    FOREIGN KEY (TIME_ID) REFERENCES RESERVATION_TIME (ID),
    FOREIGN KEY (THEME_ID) REFERENCES THEME (ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (ID)
);

