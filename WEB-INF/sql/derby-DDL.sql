CREATE TABLE APP.Bom
(rid INTEGER NOT NULL,
name VARCHAR(25),
ver  VARCHAR(4),
url  VARCHAR(45),
expiration DATE,
grade  VARCHAR(4),
module  VARCHAR(4),
category  VARCHAR(4),
md5  VARCHAR(40),
PRIMARY KEY (rid)
)

ALTER TABLE APP.Bom ADD md5 VARCHAR(40)
ALTER TABLE APP.Bom ADD lorder INTEGER	
ALTER TABLE APP.Bom ALTER COLUMN url SET DATA TYPE varchar(150)