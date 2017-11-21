CREATE TABLE `bom` (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `ver` int(11) DEFAULT NULL,
  `url` varchar(250) DEFAULT NULL,
  `grade` varchar(12) DEFAULT NULL,
  `module` varchar(12) DEFAULT NULL,
  `category` varchar(2) DEFAULT NULL,
  `md5` varchar(45) DEFAULT NULL,
  `lorder` int(11) DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='NGO versioning BOM';

CREATE TABLE `module` (
  `mid` varchar(20) NOT NULL,
  `gid` varchar(20),
  `name` VARCHAR(45),
  `description`  VARCHAR(250),
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='NGO resource module';

CREATE TABLE `grade` (
  `gid` varchar(20),
  `name` VARCHAR(45),
  `description`  VARCHAR(250),
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='NGO resource grade';
