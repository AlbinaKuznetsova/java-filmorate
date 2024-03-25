INSERT INTO RATINGMPA (RATINGMPAID, NAME) SELECT 1, 'G' FROM DUAL WHERE NOT EXISTS (SELECT * FROM RATINGMPA WHERE RATINGMPAID = 1);
INSERT INTO RATINGMPA (RATINGMPAID, NAME) SELECT 2, 'PG' FROM DUAL WHERE NOT EXISTS (SELECT * FROM RATINGMPA WHERE RATINGMPAID = 2);
INSERT INTO RATINGMPA (RATINGMPAID, NAME) SELECT 3, 'PG-13' FROM DUAL WHERE NOT EXISTS (SELECT * FROM RATINGMPA WHERE RATINGMPAID = 3);
INSERT INTO RATINGMPA (RATINGMPAID, NAME) SELECT 4, 'R' FROM DUAL WHERE NOT EXISTS (SELECT * FROM RATINGMPA WHERE RATINGMPAID = 4);
INSERT INTO RATINGMPA (RATINGMPAID, NAME) SELECT 5, 'NC-17' FROM DUAL WHERE NOT EXISTS (SELECT * FROM RATINGMPA WHERE RATINGMPAID = 5);

INSERT INTO GENRES (GENREID, NAME) SELECT 1, 'Комедия' FROM DUAL WHERE NOT EXISTS (SELECT * FROM GENRES WHERE GENREID = 1);
INSERT INTO GENRES (GENREID, NAME) SELECT 2, 'Драма' FROM DUAL WHERE NOT EXISTS (SELECT * FROM GENRES WHERE GENREID = 2);
INSERT INTO GENRES (GENREID, NAME) SELECT 3, 'Мультфильм' FROM DUAL WHERE NOT EXISTS (SELECT * FROM GENRES WHERE GENREID = 3);
INSERT INTO GENRES (GENREID, NAME) SELECT 4, 'Триллер' FROM DUAL WHERE NOT EXISTS (SELECT * FROM GENRES WHERE GENREID = 4);
INSERT INTO GENRES (GENREID, NAME) SELECT 5, 'Документальный' FROM DUAL WHERE NOT EXISTS (SELECT * FROM GENRES WHERE GENREID = 5);
INSERT INTO GENRES (GENREID, NAME) SELECT 6, 'Боевик' FROM DUAL WHERE NOT EXISTS (SELECT * FROM GENRES WHERE GENREID = 6);