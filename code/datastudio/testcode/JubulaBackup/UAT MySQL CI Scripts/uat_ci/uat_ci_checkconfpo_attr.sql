-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 10.18.96.229    Database: uat_ci
-- ------------------------------------------------------
-- Server version	5.5.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `checkconfpo_attr`
--

DROP TABLE IF EXISTS `checkconfpo_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkconfpo_attr` (
  `CheckConfPO_ID` bigint(20) DEFAULT NULL,
  `ATTR` varchar(255) DEFAULT NULL,
  `ATTR_KEY` varchar(255) DEFAULT NULL,
  KEY `FK_CheckConfPO_ATTR_CheckConfPO_ID` (`CheckConfPO_ID`),
  CONSTRAINT `FK_CheckConfPO_ATTR_CheckConfPO_ID` FOREIGN KEY (`CheckConfPO_ID`) REFERENCES `check_conf` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkconfpo_attr`
--

LOCK TABLES `checkconfpo_attr` WRITE;
/*!40000 ALTER TABLE `checkconfpo_attr` DISABLE KEYS */;
INSERT INTO `checkconfpo_attr` VALUES (3751,'5','quantity'),(3753,'WORK','name'),(3756,'15','quantity'),(3755,'FULLTEST','name contains'),(3744,'Event Handler','name'),(3754,'BROKEN','name contains'),(4675,'Event Handler','name'),(4687,'15','quantity'),(4684,'WORK','name'),(4686,'FULLTEST','name contains'),(4685,'BROKEN','name contains'),(4682,'5','quantity'),(5261,'Event Handler','name'),(5273,'15','quantity'),(5271,'BROKEN','name contains'),(5272,'FULLTEST','name contains'),(5268,'5','quantity'),(5270,'WORK','name'),(5799,'5','quantity'),(5792,'Event Handler','name'),(5802,'BROKEN','name contains'),(5803,'FULLTEST','name contains'),(5804,'15','quantity'),(5801,'WORK','name'),(6292,'15','quantity'),(6291,'FULLTEST','name contains'),(6290,'BROKEN','name contains'),(6287,'5','quantity'),(6289,'WORK','name'),(6280,'Event Handler','name'),(217215,'FULLTEST','name contains'),(217214,'BROKEN','name contains'),(217211,'5','quantity'),(217216,'15','quantity'),(217204,'Event Handler','name'),(217213,'WORK','name');
/*!40000 ALTER TABLE `checkconfpo_attr` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-28 16:52:52
