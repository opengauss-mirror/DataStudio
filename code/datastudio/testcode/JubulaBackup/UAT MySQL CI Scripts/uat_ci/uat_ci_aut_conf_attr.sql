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
-- Table structure for table `aut_conf_attr`
--

DROP TABLE IF EXISTS `aut_conf_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aut_conf_attr` (
  `AUT_CONF` bigint(20) DEFAULT NULL,
  `ATTR_VALUE` varchar(4000) DEFAULT NULL,
  `ATTR_KEY` varchar(255) DEFAULT NULL,
  KEY `FK_AUT_CONF_ATTR_AUT_CONF` (`AUT_CONF`),
  CONSTRAINT `FK_AUT_CONF_ATTR_AUT_CONF` FOREIGN KEY (`AUT_CONF`) REFERENCES `aut_conf` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aut_conf_attr`
--

LOCK TABLES `aut_conf_attr` WRITE;
/*!40000 ALTER TABLE `aut_conf_attr` DISABLE KEYS */;
INSERT INTO `aut_conf_attr` VALUES (26356,'C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\plugins\\com.huawei.mppdbide.presentation_1.0.0.201804050544.jar;C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\plugins\\com.huawei.mppdbide.util_1.0.0.201804050544.jar;C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\plugins\\com.huawei.mppdbide.explainplan_1.0.0.201804050544.jar;C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\plugins\\com.huawei.mppdbide.bl_1.0.0.201804050544.jar','INSTALL_DIR'),(26356,'../jre/bin/java.exe','JRE_BINARY'),(26356,'C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio\\Data Studio.exe','EXECUTABLE'),(26356,'Jacoco','MONITORING_AGENT_ID'),(26356,'C:\\Project_DB_Tool_Automation_Suite\\IDE\\IDE_Tool\\Data Studio','WORKING_DIR'),(26356,'V1R3_Automation@localhost','CONFIG_NAME'),(26356,'localhost','SERVER'),(26356,'en_US','KEYBOARD_LAYOUT'),(26356,'D:\\Gauss_Tools_18_DS\\code\\datastudio\\src\\com.huawei.mppdbide.presentation\\src\\com\\huawei\\mppdbide\\presentation;D:\\Gauss_Tools_18_DS\\code\\datastudio\\src\\com.huawei.mppdbide.utils\\src\\com\\huawei\\mppdbide\\utils\\exceptions;D:\\Gauss_Tools_18_DS\\code\\datastudio\\src\\com.huawei.mppdbide.explainplan;D:\\Gauss_Tools_18_DS\\code\\datastudio\\src\\com.huawei.mppdbide.bl\\src\\com\\huawei\\mppdbide\\bl','SOURCE_DIRS'),(26356,'V1R3_Automation','AUT_ID'),(26356,'true','CLASS_FILE_ID_COLLISION'),(26356,'true','RESET_AGENT');
/*!40000 ALTER TABLE `aut_conf_attr` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-28 16:55:23