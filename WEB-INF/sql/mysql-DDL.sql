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
  `mid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `grade` int(11) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `lorder` int(11) DEFAULT NULL,
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='NGO resource module';
