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
-- Table structure for table `checkconfpo_contexts`
--

DROP TABLE IF EXISTS `checkconfpo_contexts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `checkconfpo_contexts` (
  `CheckConfPO_ID` bigint(20) DEFAULT NULL,
  `CONTEXTS` tinyint(1) DEFAULT '0',
  `CONTEXTS_KEY` varchar(255) DEFAULT NULL,
  KEY `FK_CheckConfPO_CONTEXTS_CheckConfPO_ID` (`CheckConfPO_ID`),
  CONSTRAINT `FK_CheckConfPO_CONTEXTS_CheckConfPO_ID` FOREIGN KEY (`CheckConfPO_ID`) REFERENCES `check_conf` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkconfpo_contexts`
--

LOCK TABLES `checkconfpo_contexts` WRITE;
/*!40000 ALTER TABLE `checkconfpo_contexts` DISABLE KEYS */;
INSERT INTO `checkconfpo_contexts` VALUES (3760,1,'CapContext'),(3760,1,'SpecTestCaseContext'),(3760,1,'TestJobContext'),(3760,1,'TestSuiteContext'),(3760,1,'CategoryContext'),(3747,1,'TestJobContext'),(3747,1,'TestSuiteContext'),(3747,1,'ProjectContext'),(3750,1,'TestSuiteContext'),(3754,1,'ProjectContext'),(3745,1,'SpecTestCaseContext'),(3753,1,'ProjectContext'),(3748,1,'ProjectContext'),(3756,1,'ProjectContext'),(3756,1,'CategoryContext'),(3751,1,'SpecTestCaseContext'),(3743,1,'TestSuiteContext'),(3757,1,'ProjectContext'),(3755,1,'ProjectContext'),(3758,1,'SpecTestCaseContext'),(3758,1,'TestSuiteContext'),(3752,1,'ExecTestCaseContext'),(3744,1,'ProjectContext'),(3761,1,'SpecTestCaseContext'),(3761,1,'TestSuiteContext'),(3749,1,'SpecTestCaseContext'),(3759,1,'ObjectMappingCategoryContext'),(3759,1,'ExecTestCaseContext'),(3759,1,'CentralTestDataContext'),(3759,1,'SpecTestCaseContext'),(3759,1,'TestJobContext'),(3759,1,'TestSuiteContext'),(3759,1,'ComponentNameContext'),(3759,1,'CategoryContext'),(3759,1,'ProjectContext'),(3746,1,'ExecTestCaseContext'),(3746,1,'SpecTestCaseContext'),(4679,1,'TestSuiteContext'),(4676,1,'TestJobContext'),(4676,1,'TestSuiteContext'),(4676,1,'ProjectContext'),(4680,1,'SpecTestCaseContext'),(4673,1,'ProjectContext'),(4683,1,'ProjectContext'),(4675,1,'ExecTestCaseContext'),(4675,1,'SpecTestCaseContext'),(4688,1,'ObjectMappingCategoryContext'),(4688,1,'ExecTestCaseContext'),(4688,1,'CentralTestDataContext'),(4688,1,'SpecTestCaseContext'),(4688,1,'TestJobContext'),(4688,1,'TestSuiteContext'),(4688,1,'ComponentNameContext'),(4688,1,'CategoryContext'),(4688,1,'ProjectContext'),(4686,1,'ProjectContext'),(4672,1,'TestSuiteContext'),(4674,1,'SpecTestCaseContext'),(4689,1,'CapContext'),(4689,1,'SpecTestCaseContext'),(4689,1,'TestJobContext'),(4689,1,'TestSuiteContext'),(4689,1,'CategoryContext'),(4684,1,'ProjectContext'),(4681,1,'ExecTestCaseContext'),(4677,1,'ProjectContext'),(4682,1,'ProjectContext'),(4685,1,'ProjectContext'),(4685,1,'CategoryContext'),(4687,1,'SpecTestCaseContext'),(4687,1,'TestSuiteContext'),(4678,1,'SpecTestCaseContext'),(4690,1,'SpecTestCaseContext'),(4690,1,'TestSuiteContext'),(5275,1,'CapContext'),(5275,1,'SpecTestCaseContext'),(5275,1,'TestJobContext'),(5275,1,'TestSuiteContext'),(5275,1,'CategoryContext'),(5269,1,'ProjectContext'),(5266,1,'SpecTestCaseContext'),(5270,1,'ProjectContext'),(5261,1,'ExecTestCaseContext'),(5261,1,'SpecTestCaseContext'),(5259,1,'ProjectContext'),(5265,1,'TestSuiteContext'),(5262,1,'TestJobContext'),(5262,1,'TestSuiteContext'),(5262,1,'ProjectContext'),(5271,1,'ProjectContext'),(5271,1,'CategoryContext'),(5276,1,'SpecTestCaseContext'),(5276,1,'TestSuiteContext'),(5264,1,'SpecTestCaseContext'),(5260,1,'SpecTestCaseContext'),(5263,1,'ProjectContext'),(5274,1,'ObjectMappingCategoryContext'),(5274,1,'ExecTestCaseContext'),(5274,1,'CentralTestDataContext'),(5274,1,'SpecTestCaseContext'),(5274,1,'TestJobContext'),(5274,1,'TestSuiteContext'),(5274,1,'ComponentNameContext'),(5274,1,'CategoryContext'),(5274,1,'ProjectContext'),(5273,1,'SpecTestCaseContext'),(5273,1,'TestSuiteContext'),(5258,1,'TestSuiteContext'),(5267,1,'ExecTestCaseContext'),(5272,1,'ProjectContext'),(5268,1,'ProjectContext'),(5804,1,'SpecTestCaseContext'),(5804,1,'TestSuiteContext'),(5797,1,'SpecTestCaseContext'),(5794,1,'ProjectContext'),(5793,1,'TestJobContext'),(5793,1,'TestSuiteContext'),(5793,1,'ProjectContext'),(5791,1,'SpecTestCaseContext'),(5789,1,'TestSuiteContext'),(5796,1,'TestSuiteContext'),(5802,1,'ProjectContext'),(5802,1,'CategoryContext'),(5803,1,'ProjectContext'),(5792,1,'ExecTestCaseContext'),(5792,1,'SpecTestCaseContext'),(5806,1,'CapContext'),(5806,1,'SpecTestCaseContext'),(5806,1,'TestJobContext'),(5806,1,'TestSuiteContext'),(5806,1,'CategoryContext'),(5807,1,'SpecTestCaseContext'),(5807,1,'TestSuiteContext'),(5805,1,'ObjectMappingCategoryContext'),(5805,1,'ExecTestCaseContext'),(5805,1,'CentralTestDataContext'),(5805,1,'SpecTestCaseContext'),(5805,1,'TestJobContext'),(5805,1,'TestSuiteContext'),(5805,1,'ComponentNameContext'),(5805,1,'CategoryContext'),(5805,1,'ProjectContext'),(5799,1,'ProjectContext'),(5798,1,'ExecTestCaseContext'),(5790,1,'ProjectContext'),(5795,1,'SpecTestCaseContext'),(5800,1,'ProjectContext'),(5801,1,'ProjectContext'),(6286,1,'SpecTestCaseContext'),(6290,1,'ProjectContext'),(6280,1,'SpecTestCaseContext'),(6289,1,'ProjectContext'),(6287,1,'ExecTestCaseContext'),(6281,1,'ExecTestCaseContext'),(6281,1,'SpecTestCaseContext'),(6291,1,'ProjectContext'),(6291,1,'CategoryContext'),(6285,1,'TestSuiteContext'),(6292,1,'ProjectContext'),(6284,1,'SpecTestCaseContext'),(6282,1,'TestJobContext'),(6282,1,'TestSuiteContext'),(6282,1,'ProjectContext'),(6278,1,'TestSuiteContext'),(6283,1,'ProjectContext'),(6295,1,'CapContext'),(6295,1,'SpecTestCaseContext'),(6295,1,'TestJobContext'),(6295,1,'TestSuiteContext'),(6295,1,'CategoryContext'),(6293,1,'SpecTestCaseContext'),(6293,1,'TestSuiteContext'),(6294,1,'ObjectMappingCategoryContext'),(6294,1,'ExecTestCaseContext'),(6294,1,'CentralTestDataContext'),(6294,1,'SpecTestCaseContext'),(6294,1,'TestJobContext'),(6294,1,'TestSuiteContext'),(6294,1,'ComponentNameContext'),(6294,1,'CategoryContext'),(6294,1,'ProjectContext'),(6288,1,'ProjectContext'),(6279,1,'ProjectContext'),(6296,1,'SpecTestCaseContext'),(6296,1,'TestSuiteContext'),(157442,1,'ProjectContext'),(157442,1,'CategoryContext'),(157439,1,'ProjectContext'),(157435,1,'SpecTestCaseContext'),(157430,1,'ProjectContext'),(157431,1,'SpecTestCaseContext'),(157437,1,'SpecTestCaseContext'),(157445,1,'ObjectMappingCategoryContext'),(157445,1,'ExecTestCaseContext'),(157445,1,'CentralTestDataContext'),(157445,1,'SpecTestCaseContext'),(157445,1,'TestJobContext'),(157445,1,'TestSuiteContext'),(157445,1,'ComponentNameContext'),(157445,1,'CategoryContext'),(157445,1,'ProjectContext'),(157443,1,'ProjectContext'),(157436,1,'TestSuiteContext'),(157446,1,'CapContext'),(157446,1,'SpecTestCaseContext'),(157446,1,'TestJobContext'),(157446,1,'TestSuiteContext'),(157446,1,'CategoryContext'),(157447,1,'SpecTestCaseContext'),(157447,1,'TestSuiteContext'),(157444,1,'SpecTestCaseContext'),(157444,1,'TestSuiteContext'),(157441,1,'ProjectContext'),(157438,1,'ExecTestCaseContext'),(157429,1,'TestSuiteContext'),(157433,1,'TestJobContext'),(157433,1,'TestSuiteContext'),(157433,1,'ProjectContext'),(157432,1,'ExecTestCaseContext'),(157432,1,'SpecTestCaseContext'),(157440,1,'ProjectContext'),(157434,1,'ProjectContext'),(361488,1,'ProjectContext'),(361500,1,'CapContext'),(361500,1,'SpecTestCaseContext'),(361500,1,'TestJobContext'),(361500,1,'TestSuiteContext'),(361500,1,'CategoryContext'),(361493,1,'ProjectContext'),(361485,1,'SpecTestCaseContext'),(361487,1,'TestJobContext'),(361487,1,'TestSuiteContext'),(361487,1,'ProjectContext'),(361496,1,'ProjectContext'),(361496,1,'CategoryContext'),(361499,1,'ObjectMappingCategoryContext'),(361499,1,'ExecTestCaseContext'),(361499,1,'CentralTestDataContext'),(361499,1,'SpecTestCaseContext'),(361499,1,'TestJobContext'),(361499,1,'TestSuiteContext'),(361499,1,'ComponentNameContext'),(361499,1,'CategoryContext'),(361499,1,'ProjectContext'),(361492,1,'ExecTestCaseContext'),(361484,1,'ProjectContext'),(361498,1,'SpecTestCaseContext'),(361498,1,'TestSuiteContext'),(361483,1,'TestSuiteContext'),(361501,1,'SpecTestCaseContext'),(361501,1,'TestSuiteContext'),(361495,1,'ProjectContext'),(361489,1,'SpecTestCaseContext'),(361497,1,'ProjectContext'),(361491,1,'SpecTestCaseContext'),(361494,1,'ProjectContext'),(361490,1,'TestSuiteContext'),(361486,1,'ExecTestCaseContext'),(361486,1,'SpecTestCaseContext');
/*!40000 ALTER TABLE `checkconfpo_contexts` ENABLE KEYS */;
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
