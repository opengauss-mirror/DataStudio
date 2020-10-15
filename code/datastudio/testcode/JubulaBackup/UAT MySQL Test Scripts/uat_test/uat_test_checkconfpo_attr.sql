-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 10.18.96.229    Database: uat_test
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
INSERT INTO `checkconfpo_attr` VALUES (3754,'BROKEN','name contains'),(3753,'WORK','name'),(3756,'15','quantity'),(3751,'5','quantity'),(3755,'FULLTEST','name contains'),(3744,'Event Handler','name'),(4680,'5','quantity'),(4673,'Event Handler','name'),(4683,'BROKEN','name contains'),(4684,'FULLTEST','name contains'),(4682,'WORK','name'),(4685,'15','quantity'),(5269,'BROKEN','name contains'),(5266,'5','quantity'),(5270,'FULLTEST','name contains'),(5259,'Event Handler','name'),(5271,'15','quantity'),(5268,'WORK','name'),(5797,'5','quantity'),(5802,'15','quantity'),(5799,'WORK','name'),(5790,'Event Handler','name'),(5800,'BROKEN','name contains'),(5801,'FULLTEST','name contains'),(6286,'5','quantity'),(6290,'FULLTEST','name contains'),(6289,'BROKEN','name contains'),(6291,'15','quantity'),(6288,'WORK','name'),(6279,'Event Handler','name'),(157442,'15','quantity'),(157439,'WORK','name'),(157430,'Event Handler','name'),(157437,'5','quantity'),(157441,'FULLTEST','name contains'),(157440,'BROKEN','name contains'),(361493,'WORK','name'),(361496,'15','quantity'),(361484,'Event Handler','name'),(361495,'FULLTEST','name contains'),(361491,'5','quantity'),(361494,'BROKEN','name contains');
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

-- Dump completed on 2018-06-29 15:43:50
