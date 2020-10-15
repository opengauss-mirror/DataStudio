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
-- Table structure for table `check_conf_cont_map`
--

DROP TABLE IF EXISTS `check_conf_cont_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `check_conf_cont_map` (
  `CheckConfContPO_ID` bigint(20) NOT NULL,
  `confMap_ID` bigint(20) NOT NULL,
  `CHECK_CONF_KEY` varchar(255) NOT NULL,
  PRIMARY KEY (`CheckConfContPO_ID`,`confMap_ID`),
  KEY `FK_CHECK_CONF_CONT_MAP_confMap_ID` (`confMap_ID`),
  CONSTRAINT `FK_CHECK_CONF_CONT_MAP_CheckConfContPO_ID` FOREIGN KEY (`CheckConfContPO_ID`) REFERENCES `check_conf_cont` (`ID`),
  CONSTRAINT `FK_CHECK_CONF_CONT_MAP_confMap_ID` FOREIGN KEY (`confMap_ID`) REFERENCES `check_conf` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `check_conf_cont_map`
--

LOCK TABLES `check_conf_cont_map` WRITE;
/*!40000 ALTER TABLE `check_conf_cont_map` DISABLE KEYS */;
INSERT INTO `check_conf_cont_map` VALUES (3742,3743,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(3742,3744,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(3742,3745,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(3742,3746,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(3742,3747,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(3742,3748,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(3742,3749,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(3742,3750,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(3742,3751,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(3742,3752,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(3742,3753,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(3742,3754,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(3742,3755,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(3742,3756,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(3742,3757,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(3742,3758,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(3742,3759,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(3742,3760,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(3742,3761,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps'),(4671,4672,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(4671,4673,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(4671,4674,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(4671,4675,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(4671,4676,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(4671,4677,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(4671,4678,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(4671,4679,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(4671,4680,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(4671,4681,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(4671,4682,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(4671,4683,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(4671,4684,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(4671,4685,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(4671,4686,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(4671,4687,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(4671,4688,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(4671,4689,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(4671,4690,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps'),(5257,5258,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(5257,5259,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(5257,5260,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(5257,5261,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(5257,5262,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(5257,5263,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(5257,5264,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(5257,5265,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(5257,5266,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(5257,5267,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(5257,5268,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(5257,5269,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(5257,5270,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(5257,5271,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(5257,5272,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(5257,5273,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(5257,5274,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(5257,5275,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(5257,5276,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps'),(5788,5789,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(5788,5790,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(5788,5791,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(5788,5792,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(5788,5793,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(5788,5794,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(5788,5795,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(5788,5796,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(5788,5797,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(5788,5798,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(5788,5799,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(5788,5800,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(5788,5801,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(5788,5802,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(5788,5803,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(5788,5804,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(5788,5805,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(5788,5806,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(5788,5807,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps'),(6277,6278,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(6277,6279,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(6277,6280,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(6277,6281,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(6277,6282,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(6277,6283,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(6277,6284,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(6277,6285,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(6277,6286,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(6277,6287,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(6277,6288,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(6277,6289,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(6277,6290,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(6277,6291,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(6277,6292,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(6277,6293,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(6277,6294,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(6277,6295,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(6277,6296,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps'),(157428,157429,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(157428,157430,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(157428,157431,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(157428,157432,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(157428,157433,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(157428,157434,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(157428,157435,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(157428,157436,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(157428,157437,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(157428,157438,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(157428,157439,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(157428,157440,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(157428,157441,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(157428,157442,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(157428,157443,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(157428,157444,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(157428,157445,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(157428,157446,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(157428,157447,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps'),(361482,361483,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteHasAUT'),(361482,361484,'com.bredexsw.guidancer.client.teststyle.bredexstyle.eventhandler'),(361482,361485,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametername'),(361482,361486,'com.bredexsw.guidancer.client.teststyle.bredexstyle.excel'),(361482,361487,'com.bredexsw.guidancer.client.teststyle.bredexstyle.nospaces'),(361482,361488,'com.bredexsw.guidancer.client.teststyle.bredexstyle.reused'),(361482,361489,'com.bredexsw.guidancer.client.teststyle.bredexstyle.deprecated'),(361482,361490,'org.eclipse.jubula.client.teststyle.impl.standard.checks.testSuiteNotEmpty'),(361482,361491,'com.bredexsw.guidancer.client.teststyle.bredexstyle.parametersquantity'),(361482,361492,'com.bredexsw.guidancer.client.teststyle.bredexstyle.missingname'),(361482,361493,'com.bredexsw.guidancer.client.teststyle.bredexstyle.check8'),(361482,361494,'com.bredexsw.guidancer.client.teststyle.bredexstyle.broken'),(361482,361495,'com.bredexsw.guidancer.client.teststyle.bredexstyle.fulltest'),(361482,361496,'com.bredexsw.guidancer.client.teststyle.bredexstyle.childrennumber'),(361482,361497,'com.bredexsw.guidancer.client.teststyle.bredexstyle.centraltestdata'),(361482,361498,'com.bredexsw.guidancer.client.teststyle.bredexstyle.commented'),(361482,361499,'com.bredexsw.guidancer.client.teststyle.bredexstyle.spaces'),(361482,361500,'com.bredexsw.guidancer.client.teststyle.bredexstyle.defaultname'),(361482,361501,'com.bredexsw.guidancer.client.teststyle.bredexstyle.teststeps');
/*!40000 ALTER TABLE `check_conf_cont_map` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-29 15:44:54