DROP TABLE IF EXISTS contacts;

CREATE TABLE contacts 
(
id SERIAL NOT NULL,
firstName varchar(50) NOT NULL,
lastName varchar(50) NOT NULL,
email varchar(256),
phoneNumber varchar(256),
PRIMARY KEY (id)
);