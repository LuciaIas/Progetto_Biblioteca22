-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: biblioteca
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `autori`
--

DROP TABLE IF EXISTS `autori`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `autori` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(80) NOT NULL,
  `num_opere` int(11) DEFAULT 0,
  `data_nascita` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autori`
--

LOCK TABLES `autori` WRITE;
/*!40000 ALTER TABLE `autori` DISABLE KEYS */;
INSERT INTO `autori` VALUES (1,'1','1',0,NULL),(2,'1','hui',0,NULL),(3,'Nuovo','autore',0,NULL),(9,'gh','bn',0,NULL);
/*!40000 ALTER TABLE `autori` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bibliotecario`
--

DROP TABLE IF EXISTS `bibliotecario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bibliotecario` (
  `password_` varchar(200) NOT NULL,
  PRIMARY KEY (`password_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bibliotecario`
--

LOCK TABLES `bibliotecario` WRITE;
/*!40000 ALTER TABLE `bibliotecario` DISABLE KEYS */;
INSERT INTO `bibliotecario` VALUES ('2ac6f3dca460835154767820682222d9d1a12775f72fe9de463c0ef2f3fe98d9');
/*!40000 ALTER TABLE `bibliotecario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `libri`
--

DROP TABLE IF EXISTS `libri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `libri` (
  `isbn` char(13) NOT NULL,
  `titolo` varchar(150) NOT NULL,
  `editore` varchar(100) NOT NULL,
  `anno_pubblicazione` int(11) NOT NULL,
  `num_copie` int(11) DEFAULT 0,
  `url_immagine` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `libri`
--

LOCK TABLES `libri` WRITE;
/*!40000 ALTER TABLE `libri` DISABLE KEYS */;
INSERT INTO `libri` VALUES ('1234567890123','George Orwell','',2024,0,'file:/C:/Users/nicol/Documents/NetBeansProjects/PROGETTOUNI/src/main/resources/Images/9780451524935_p0_v3_s550x406.jpg'),('1234567890124','The Handmad\'S Tale','',2024,0,'file:/C:/Users/nicol/Documents/NetBeansProjects/PROGETTOUNI/src/main/resources/Images/famous-book-covers-the-handmaids-tale.jpg'),('1234567890125','The Little Prince','',2024,0,'file:/C:/Users/nicol/Documents/NetBeansProjects/PROGETTOUNI/src/main/resources/Images/famous-book-covers-the-little-prince-675x1024.jpg'),('1234567890126','Odi Et Amo','',2024,1,'file:/C:/Users/nicol/Documents/NetBeansProjects/PROGETTOUNI/src/main/resources/Images/images.jpg'),('1234567890127','Essentials of Software Engineering','GRUPPO22UNISA',2024,0,'file:/C:/Users/nicol/Documents/NetBeansProjects/PROGETTOUNI/src/main/resources/Images/Immagine%202025-12-05%20220522.png'),('TEST_ISBN_99','Test Book','Ed',2025,5,'');
/*!40000 ALTER TABLE `libri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prestito`
--

DROP TABLE IF EXISTS `prestito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prestito` (
  `isbn` varchar(13) NOT NULL,
  `matricola` varchar(10) NOT NULL,
  `data_prestito` date NOT NULL DEFAULT curdate(),
  `data_restituzione` date DEFAULT NULL,
  `stato_prestito` enum('ATTIVO','RESTITUITO','PROROGATO','IN_RITARDO') DEFAULT 'ATTIVO',
  `data_scadenza` date DEFAULT NULL,
  PRIMARY KEY (`isbn`,`matricola`),
  KEY `matricola` (`matricola`),
  CONSTRAINT `prestito_ibfk_1` FOREIGN KEY (`isbn`) REFERENCES `libri` (`isbn`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `prestito_ibfk_2` FOREIGN KEY (`matricola`) REFERENCES `utenti` (`matricola`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prestito`
--

LOCK TABLES `prestito` WRITE;
/*!40000 ALTER TABLE `prestito` DISABLE KEYS */;
INSERT INTO `prestito` VALUES ('1234567890123','1234567890','2025-12-05',NULL,'PROROGATO','2025-12-21'),('1234567890124','1234567892','2025-12-05',NULL,'PROROGATO','2025-12-21'),('1234567890125','1234567892','2025-12-12','2025-12-09','RESTITUITO','2025-12-29'),('1234567890125','1234567893','2025-12-09',NULL,'ATTIVO','2025-12-16');
/*!40000 ALTER TABLE `prestito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scritto_da`
--

DROP TABLE IF EXISTS `scritto_da`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scritto_da` (
  `isbn` varchar(13) NOT NULL,
  `id_autore` int(11) NOT NULL,
  PRIMARY KEY (`isbn`,`id_autore`),
  KEY `id_autore` (`id_autore`),
  CONSTRAINT `scritto_da_ibfk_1` FOREIGN KEY (`isbn`) REFERENCES `libri` (`isbn`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `scritto_da_ibfk_2` FOREIGN KEY (`id_autore`) REFERENCES `autori` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scritto_da`
--

LOCK TABLES `scritto_da` WRITE;
/*!40000 ALTER TABLE `scritto_da` DISABLE KEYS */;
/*!40000 ALTER TABLE `scritto_da` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utenti`
--

DROP TABLE IF EXISTS `utenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `utenti` (
  `matricola` varchar(10) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `cognome` varchar(80) NOT NULL,
  `mail` varchar(250) NOT NULL,
  `Bloccato` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`matricola`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utenti`
--

LOCK TABLES `utenti` WRITE;
/*!40000 ALTER TABLE `utenti` DISABLE KEYS */;
INSERT INTO `utenti` VALUES ('1234567890','Nicola','Miranda','n.miranda6@studenti.unisa.it',0),('1234567891','Michele','Tamburro','m.tamburro@studenti.unisa.it',1),('1234567892','Lucia','Monetta','l.monetta8@studenti.unisa.it',0),('1234567893','Lucia','Iasevoli','l.iasevoli1@studenti.unisa.it',0);
/*!40000 ALTER TABLE `utenti` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-12 10:40:49
