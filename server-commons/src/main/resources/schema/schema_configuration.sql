DROP TABLE IF EXISTS configuration;

CREATE TABLE configuration
(
id SERIAL NOT NULL,
configKey varchar(100) UNIQUE NOT NULL,
configValue text NOT NULL,
dataType varchar(16) NOT NULL,
PRIMARY KEY (id)
);