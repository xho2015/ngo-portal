DROP TABLE APP.Bom;
CREATE TABLE APP.Bom
(
rid INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
name VARCHAR(45),
ver  VARCHAR(4),
url  VARCHAR(250),
grade  VARCHAR(20),
module  VARCHAR(20),
category  VARCHAR(4),
md5  VARCHAR(40),
lorder INTEGER,
CONSTRAINT PK_BOM_RID PRIMARY KEY (rid)
);


DROP TABLE APP.Module;
CREATE TABLE APP.Module
(
mid VARCHAR(20) NOT NULL,
gid VARCHAR(20),
name VARCHAR(45),
description  VARCHAR(250),
CONSTRAINT PK_MODULE_MID PRIMARY KEY (mid)
);

--sample for alert table
/*
ALTER TABLE APP.Bom ADD md5 VARCHAR(40)
ALTER TABLE APP.Bom ADD lorder INTEGER	
ALTER TABLE APP.Bom ALTER COLUMN url SET DATA TYPE varchar(150)
*/

--sample for adding RID as auto-increase
/*
ALTER TABLE App.Bom DROP COLUMN RID ;
ALTER TABLE App.Bom ADD RID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1);
*/

--drap table sample

--create table sample
/*
CREATE TABLE students
(
id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
name VARCHAR(24) NOT NULL,
address VARCHAR(1024),
CONSTRAINT primary_key PRIMARY KEY (id)
) ;
*/