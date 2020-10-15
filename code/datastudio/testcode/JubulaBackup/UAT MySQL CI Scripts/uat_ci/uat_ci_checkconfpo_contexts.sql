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
INSERT INTO `checkconfpo_contexts` VALUES (3745,1,'SpecTestCaseContext'),(3760,1,'CapContext'),(3760,1,'SpecTestCaseContext'),(3760,1,'TestJobContext'),(3760,1,'TestSuiteContext'),(3760,1,'CategoryContext'),(3751,1,'SpecTestCaseContext'),(3759,1,'ObjectMappingCategoryContext'),(3759,1,'ExecTestCaseContext'),(3759,1,'CentralTestDataContext'),(3759,1,'SpecTestCaseContext'),(3759,1,'TestJobContext'),(3759,1,'TestSuiteContext'),(3759,1,'ComponentNameContext'),(3759,1,'CategoryContext'),(3759,1,'ProjectContext'),(3749,1,'SpecTestCaseContext'),(3758,1,'SpecTestCaseContext'),(3758,1,'TestSuiteContext'),(3761,1,'SpecTestCaseContext'),(3761,1,'TestSuiteContext'),(3748,1,'ProjectContext'),(3752,1,'ExecTestCaseContext'),(3753,1,'ProjectContext'),(3747,1,'TestJobContext'),(3747,1,'TestSuiteContext'),(3747,1,'ProjectContext'),(3750,1,'TestSuiteContext'),(3756,1,'ProjectContext'),(3756,1,'CategoryContext'),(3757,1,'ProjectContext'),(3755,1,'ProjectContext'),(3744,1,'ProjectContext'),(3746,1,'ExecTestCaseContext'),(3746,1,'SpecTestCaseContext'),(3754,1,'ProjectContext'),(3743,1,'TestSuiteContext'),(4678,1,'TestJobContext'),(4678,1,'TestSuiteContext'),(4678,1,'ProjectContext'),(4683,1,'ExecTestCaseContext'),(4677,1,'ExecTestCaseContext'),(4677,1,'SpecTestCaseContext'),(4675,1,'ProjectContext'),(4687,1,'ProjectContext'),(4687,1,'CategoryContext'),(4674,1,'TestSuiteContext'),(4689,1,'SpecTestCaseContext'),(4689,1,'TestSuiteContext'),(4692,1,'SpecTestCaseContext'),(4692,1,'TestSuiteContext'),(4688,1,'ProjectContext'),(4684,1,'ProjectContext'),(4690,1,'ObjectMappingCategoryContext'),(4690,1,'ExecTestCaseContext'),(4690,1,'CentralTestDataContext'),(4690,1,'SpecTestCaseContext'),(4690,1,'TestJobContext'),(4690,1,'TestSuiteContext'),(4690,1,'ComponentNameContext'),(4690,1,'CategoryContext'),(4690,1,'ProjectContext'),(4686,1,'ProjectContext'),(4680,1,'SpecTestCaseContext'),(4681,1,'TestSuiteContext'),(4676,1,'SpecTestCaseContext'),(4679,1,'ProjectContext'),(4691,1,'CapContext'),(4691,1,'SpecTestCaseContext'),(4691,1,'TestJobContext'),(4691,1,'TestSuiteContext'),(4691,1,'CategoryContext'),(4685,1,'ProjectContext'),(4682,1,'SpecTestCaseContext'),(5260,1,'TestSuiteContext'),(5269,1,'ExecTestCaseContext'),(5263,1,'ExecTestCaseContext'),(5263,1,'SpecTestCaseContext'),(5261,1,'ProjectContext'),(5273,1,'ProjectContext'),(5273,1,'CategoryContext'),(5278,1,'SpecTestCaseContext'),(5278,1,'TestSuiteContext'),(5262,1,'SpecTestCaseContext'),(5267,1,'TestSuiteContext'),(5265,1,'ProjectContext'),(5274,1,'ProjectContext'),(5276,1,'ObjectMappingCategoryContext'),(5276,1,'ExecTestCaseContext'),(5276,1,'CentralTestDataContext'),(5276,1,'SpecTestCaseContext'),(5276,1,'TestJobContext'),(5276,1,'TestSuiteContext'),(5276,1,'ComponentNameContext'),(5276,1,'CategoryContext'),(5276,1,'ProjectContext'),(5271,1,'ProjectContext'),(5277,1,'CapContext'),(5277,1,'SpecTestCaseContext'),(5277,1,'TestJobContext'),(5277,1,'TestSuiteContext'),(5277,1,'CategoryContext'),(5272,1,'ProjectContext'),(5268,1,'SpecTestCaseContext'),(5270,1,'ProjectContext'),(5275,1,'SpecTestCaseContext'),(5275,1,'TestSuiteContext'),(5266,1,'SpecTestCaseContext'),(5264,1,'TestJobContext'),(5264,1,'TestSuiteContext'),(5264,1,'ProjectContext'),(5799,1,'SpecTestCaseContext'),(5792,1,'ProjectContext'),(5805,1,'ProjectContext'),(5797,1,'SpecTestCaseContext'),(5793,1,'SpecTestCaseContext'),(5807,1,'ObjectMappingCategoryContext'),(5807,1,'ExecTestCaseContext'),(5807,1,'CentralTestDataContext'),(5807,1,'SpecTestCaseContext'),(5807,1,'TestJobContext'),(5807,1,'TestSuiteContext'),(5807,1,'ComponentNameContext'),(5807,1,'CategoryContext'),(5807,1,'ProjectContext'),(5795,1,'TestJobContext'),(5795,1,'TestSuiteContext'),(5795,1,'ProjectContext'),(5802,1,'ProjectContext'),(5794,1,'ExecTestCaseContext'),(5794,1,'SpecTestCaseContext'),(5791,1,'TestSuiteContext'),(5806,1,'SpecTestCaseContext'),(5806,1,'TestSuiteContext'),(5798,1,'TestSuiteContext'),(5808,1,'CapContext'),(5808,1,'SpecTestCaseContext'),(5808,1,'TestJobContext'),(5808,1,'TestSuiteContext'),(5808,1,'CategoryContext'),(5803,1,'ProjectContext'),(5809,1,'SpecTestCaseContext'),(5809,1,'TestSuiteContext'),(5804,1,'ProjectContext'),(5804,1,'CategoryContext'),(5801,1,'ProjectContext'),(5800,1,'ExecTestCaseContext'),(5796,1,'ProjectContext'),(6296,1,'CapContext'),(6296,1,'SpecTestCaseContext'),(6296,1,'TestJobContext'),(6296,1,'TestSuiteContext'),(6296,1,'CategoryContext'),(6294,1,'SpecTestCaseContext'),(6294,1,'TestSuiteContext'),(6285,1,'SpecTestCaseContext'),(6292,1,'ProjectContext'),(6292,1,'CategoryContext'),(6293,1,'ProjectContext'),(6288,1,'ExecTestCaseContext'),(6281,1,'SpecTestCaseContext'),(6291,1,'ProjectContext'),(6282,1,'ExecTestCaseContext'),(6282,1,'SpecTestCaseContext'),(6286,1,'TestSuiteContext'),(6290,1,'ProjectContext'),(6295,1,'ObjectMappingCategoryContext'),(6295,1,'ExecTestCaseContext'),(6295,1,'CentralTestDataContext'),(6295,1,'SpecTestCaseContext'),(6295,1,'TestJobContext'),(6295,1,'TestSuiteContext'),(6295,1,'ComponentNameContext'),(6295,1,'CategoryContext'),(6295,1,'ProjectContext'),(6287,1,'SpecTestCaseContext'),(6289,1,'ProjectContext'),(6283,1,'TestJobContext'),(6283,1,'TestSuiteContext'),(6283,1,'ProjectContext'),(6297,1,'SpecTestCaseContext'),(6297,1,'TestSuiteContext'),(6284,1,'ProjectContext'),(6280,1,'ProjectContext'),(6279,1,'TestSuiteContext'),(217203,1,'TestSuiteContext'),(217220,1,'CapContext'),(217220,1,'SpecTestCaseContext'),(217220,1,'TestJobContext'),(217220,1,'TestSuiteContext'),(217220,1,'CategoryContext'),(217209,1,'SpecTestCaseContext'),(217219,1,'ObjectMappingCategoryContext'),(217219,1,'ExecTestCaseContext'),(217219,1,'CentralTestDataContext'),(217219,1,'SpecTestCaseContext'),(217219,1,'TestJobContext'),(217219,1,'TestSuiteContext'),(217219,1,'ComponentNameContext'),(217219,1,'CategoryContext'),(217219,1,'ProjectContext'),(217205,1,'SpecTestCaseContext'),(217215,1,'ProjectContext'),(217206,1,'ExecTestCaseContext'),(217206,1,'SpecTestCaseContext'),(217214,1,'ProjectContext'),(217208,1,'ProjectContext'),(217212,1,'ExecTestCaseContext'),(217210,1,'TestSuiteContext'),(217218,1,'SpecTestCaseContext'),(217218,1,'TestSuiteContext'),(217221,1,'SpecTestCaseContext'),(217221,1,'TestSuiteContext'),(217217,1,'ProjectContext'),(217211,1,'SpecTestCaseContext'),(217216,1,'ProjectContext'),(217216,1,'CategoryContext'),(217204,1,'ProjectContext'),(217213,1,'ProjectContext'),(217207,1,'TestJobContext'),(217207,1,'TestSuiteContext'),(217207,1,'ProjectContext');
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

-- Dump completed on 2018-06-28 16:54:19
