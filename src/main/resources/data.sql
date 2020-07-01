DROP TABLE IF EXISTS data_table;

CREATE TABLE data_table (
  key                   VARCHAR(250) PRIMARY KEY,
  name                  VARCHAR(250) NOT NULL,
  description           VARCHAR(250) NOT NULL,
  updated_timestamp     NUMBER NOT NULL
);


INSERT INTO data_table (key, name, description, updated_timestamp) VALUES
  ('key1', 'name1', 'description1', 1593505252000),
  ('key2', 'name2', 'description2', 1593631139000);
