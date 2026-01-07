-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: campus-secondhand
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admininformation`
--

DROP TABLE IF EXISTS `admininformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admininformation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '管理员用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  `realname` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admininformation`
--

LOCK TABLES `admininformation` WRITE;
/*!40000 ALTER TABLE `admininformation` DISABLE KEYS */;
INSERT INTO `admininformation` VALUES (1,'admin','123456','系统管理员','13800138000','2025-12-08 20:39:03',NULL);
/*!40000 ALTER TABLE `admininformation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `adminoperation`
--

DROP TABLE IF EXISTS `adminoperation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adminoperation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `aid` int NOT NULL COMMENT '管理员ID',
  `operation` varchar(255) NOT NULL COMMENT '操作内容',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `aid` (`aid`),
  CONSTRAINT `adminoperation_ibfk_1` FOREIGN KEY (`aid`) REFERENCES `admininformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员操作记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adminoperation`
--

LOCK TABLES `adminoperation` WRITE;
/*!40000 ALTER TABLE `adminoperation` DISABLE KEYS */;
/*!40000 ALTER TABLE `adminoperation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `allkinds`
--

DROP TABLE IF EXISTS `allkinds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `allkinds` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `kind` varchar(50) NOT NULL COMMENT '商品种类名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `display` tinyint(1) DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_kind` (`kind`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品种类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allkinds`
--

LOCK TABLES `allkinds` WRITE;
/*!40000 ALTER TABLE `allkinds` DISABLE KEYS */;
INSERT INTO `allkinds` VALUES (1,'电子数码',1,1),(2,'图书文具',2,1),(3,'生活百货',3,1),(4,'服装鞋包',4,1),(5,'运动健身',5,1),(6,'其他商品',6,1);
/*!40000 ALTER TABLE `allkinds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `boughtshop`
--

DROP TABLE IF EXISTS `boughtshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boughtshop` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int NOT NULL COMMENT '用户ID',
  `sid` int NOT NULL COMMENT '商品ID',
  `price` decimal(10,2) NOT NULL COMMENT '购买价格',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `sid` (`sid`),
  CONSTRAINT `boughtshop_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `userinformation` (`id`),
  CONSTRAINT `boughtshop_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购买记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boughtshop`
--

LOCK TABLES `boughtshop` WRITE;
/*!40000 ALTER TABLE `boughtshop` DISABLE KEYS */;
/*!40000 ALTER TABLE `boughtshop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classification`
--

DROP TABLE IF EXISTS `classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classification` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `display` tinyint(1) DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  `pid` int DEFAULT '0' COMMENT '父分类ID',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `aid` int DEFAULT NULL COMMENT '分类关联ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classification`
--

LOCK TABLES `classification` WRITE;
/*!40000 ALTER TABLE `classification` DISABLE KEYS */;
INSERT INTO `classification` VALUES (1,'手机配件',1,1,1,'2025-12-12 17:13:21',NULL),(2,'电脑配件',2,1,1,'2025-12-12 17:13:21',NULL),(3,'数码设备',3,1,1,'2025-12-12 17:13:21',NULL),(4,'专业书籍',1,1,2,'2025-12-12 17:13:21',NULL),(5,'教材教辅',2,1,2,'2025-12-12 17:13:21',NULL),(6,'课外读物',3,1,2,'2025-12-12 17:13:21',NULL),(7,'文具用品',4,1,2,'2025-12-12 17:13:21',NULL),(8,'生活用品',1,1,3,'2025-12-12 17:13:21',NULL),(9,'小家电',2,1,3,'2025-12-12 17:13:21',NULL),(10,'美妆个护',3,1,3,'2025-12-12 17:13:21',NULL),(11,'服装',1,1,4,'2025-12-12 17:13:21',NULL),(12,'鞋包',2,1,4,'2025-12-12 17:13:21',NULL),(13,'配饰',3,1,4,'2025-12-12 17:13:21',NULL),(14,'运动器材',1,1,5,'2025-12-12 17:13:21',NULL),(15,'运动服饰',2,1,5,'2025-12-12 17:13:21',NULL),(16,'其他物品',1,1,6,'2025-12-12 17:13:21',NULL);
/*!40000 ALTER TABLE `classification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goodsoforderform`
--

DROP TABLE IF EXISTS `goodsoforderform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goodsoforderform` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `oid` int NOT NULL COMMENT '订单ID',
  `sid` int NOT NULL COMMENT '商品ID',
  `price` decimal(10,2) NOT NULL COMMENT '购买价格',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  PRIMARY KEY (`id`),
  KEY `oid` (`oid`),
  KEY `sid` (`sid`),
  CONSTRAINT `goodsoforderform_ibfk_1` FOREIGN KEY (`oid`) REFERENCES `orderform` (`id`),
  CONSTRAINT `goodsoforderform_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goodsoforderform`
--

LOCK TABLES `goodsoforderform` WRITE;
/*!40000 ALTER TABLE `goodsoforderform` DISABLE KEYS */;
/*!40000 ALTER TABLE `goodsoforderform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderform`
--

DROP TABLE IF EXISTS `orderform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderform` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `display` tinyint(1) DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  `uid` int NOT NULL COMMENT '用户ID',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `context` text COMMENT '订单备注',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `orderform_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `userinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderform`
--

LOCK TABLES `orderform` WRITE;
/*!40000 ALTER TABLE `orderform` DISABLE KEYS */;
/*!40000 ALTER TABLE `orderform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopcar`
--

DROP TABLE IF EXISTS `shopcar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopcar` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int DEFAULT NULL,
  `sid` int NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `admin_id` int DEFAULT NULL COMMENT '管理员ID',
  `user_type` tinyint(1) DEFAULT '0' COMMENT '用户类型(0普通用户,1管理员)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uid_sid` (`uid`,`sid`),
  KEY `sid` (`sid`),
  KEY `idx_car_uid` (`uid`),
  KEY `idx_car_admin_id` (`admin_id`),
  KEY `idx_car_user_type` (`user_type`),
  CONSTRAINT `fk_car_admin` FOREIGN KEY (`admin_id`) REFERENCES `admininformation` (`id`),
  CONSTRAINT `shopcar_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopcar`
--

LOCK TABLES `shopcar` WRITE;
/*!40000 ALTER TABLE `shopcar` DISABLE KEYS */;
INSERT INTO `shopcar` VALUES (1,NULL,1,1,'2025-12-12 15:17:34',NULL,0),(2,NULL,1,1,'2025-12-12 15:17:36',NULL,0),(3,NULL,1,1,'2025-12-12 15:17:55',NULL,0),(4,NULL,1,1,'2025-12-12 15:25:03',NULL,0),(5,NULL,1,1,'2025-12-12 15:25:05',NULL,0),(9,2,2,1,'2025-12-12 20:28:56',NULL,0),(10,1,3,1,'2025-12-12 23:37:50',NULL,0);
/*!40000 ALTER TABLE `shopcar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopcontext`
--

DROP TABLE IF EXISTS `shopcontext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopcontext` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sid` int NOT NULL COMMENT '商品ID',
  `context` text NOT NULL COMMENT '商品详情内容',
  PRIMARY KEY (`id`),
  KEY `sid` (`sid`),
  CONSTRAINT `shopcontext_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopcontext`
--

LOCK TABLES `shopcontext` WRITE;
/*!40000 ALTER TABLE `shopcontext` DISABLE KEYS */;
/*!40000 ALTER TABLE `shopcontext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopinformation`
--

DROP TABLE IF EXISTS `shopinformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopinformation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `level` tinyint DEFAULT '0' COMMENT '商品成色(0-10)',
  `remark` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `sort` int DEFAULT '0' COMMENT '排序',
  `display` tinyint(1) DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `transaction` tinyint(1) DEFAULT '0' COMMENT '是否交易(1已交易,0未交易)',
  `uid` int DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL COMMENT '图片路径',
  `sales` int DEFAULT '0' COMMENT '销量',
  `thumbnails` varchar(255) DEFAULT NULL COMMENT '缩略图路径',
  `audit_status` int DEFAULT '0' COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
  `admin_id` int DEFAULT NULL COMMENT '管理员ID',
  `user_type` tinyint(1) DEFAULT '0' COMMENT '用户类型(0普通用户,1管理员)',
  PRIMARY KEY (`id`),
  KEY `idx_shop_uid` (`uid`),
  KEY `idx_shop_admin_id` (`admin_id`),
  KEY `idx_shop_user_type` (`user_type`),
  CONSTRAINT `fk_shop_admin` FOREIGN KEY (`admin_id`) REFERENCES `admininformation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品信息表（包含审核状态）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopinformation`
--

LOCK TABLES `shopinformation` WRITE;
/*!40000 ALTER TABLE `shopinformation` DISABLE KEYS */;
INSERT INTO `shopinformation` VALUES (1,'2025-12-12 16:34:47','计算机网络教材',7,'qq:123456',5.00,1,0,1,0,1,'/image/U9sqzSxkHO1765523810719.jpg',0,'/images/thumbnails/T1PGPeq1GN1765523810722.jpg',1,NULL,0),(2,'2025-12-12 16:16:56','鼠标垫',10,'qq：111',10.00,11,1,1,0,1,'/image/cCzNpL80kc1765527416163.jpg',0,'/image/thumbnails/HFduHZ59Rz1765527416166.jpg',1,NULL,0),(3,'2025-12-12 22:26:42','四六级听力耳机',7,'QQ：88888888',20.00,16,1,1,0,2,'/image/wcpG2xGbK41765549601844.jpg',0,'/image/thumbnails/LUUCyMlas21765549601847.jpg',1,NULL,0),(4,'2025-12-13 00:07:46','护手霜',10,'vx：666asd',15.00,51,1,3,0,3,'/image/qruso6Wph31765555665358.jpg',0,'/image/thumbnails/wrBX4sAcCf1765555665360.jpg',0,NULL,0),(5,'2025-12-13 00:16:02','计算机网络教材',8,'带笔记QQ：123456',5.00,19,1,1,0,4,'/image/Usz8vmRLcD1765556162016.jpg',0,'/image/thumbnails/VYVZC3IMsy1765556162016.jpg',0,NULL,0),(6,'2025-12-13 01:00:31','护手霜',10,'QQ：7458980',5.00,51,1,3,0,5,'/image/GDtRkI2ZKO1765558830852.jpg',0,'/image/thumbnails/YQMRVb3EIY1765558830852.jpg',0,NULL,0);
/*!40000 ALTER TABLE `shopinformation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shoppicture`
--

DROP TABLE IF EXISTS `shoppicture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shoppicture` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sid` int NOT NULL COMMENT '商品ID',
  `image` varchar(255) NOT NULL COMMENT '图片路径',
  `sort` int DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `sid` (`sid`),
  CONSTRAINT `shoppicture_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shoppicture`
--

LOCK TABLES `shoppicture` WRITE;
/*!40000 ALTER TABLE `shoppicture` DISABLE KEYS */;
/*!40000 ALTER TABLE `shoppicture` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specific`
--

DROP TABLE IF EXISTS `specific`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specific` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `name` varchar(50) NOT NULL COMMENT '规格名称',
  `value` varchar(100) NOT NULL COMMENT '规格值',
  `sid` int NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  KEY `sid` (`sid`),
  CONSTRAINT `specific_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品规格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specific`
--

LOCK TABLES `specific` WRITE;
/*!40000 ALTER TABLE `specific` DISABLE KEYS */;
/*!40000 ALTER TABLE `specific` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specifickinds`
--

DROP TABLE IF EXISTS `specifickinds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specifickinds` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '具体分类名称',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `cid` int NOT NULL COMMENT '分类ID（外键，关联classification表）',
  PRIMARY KEY (`id`),
  KEY `cid` (`cid`),
  CONSTRAINT `specifickinds_ibfk_1` FOREIGN KEY (`cid`) REFERENCES `classification` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='具体商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specifickinds`
--

LOCK TABLES `specifickinds` WRITE;
/*!40000 ALTER TABLE `specifickinds` DISABLE KEYS */;
INSERT INTO `specifickinds` VALUES (1,'手机壳','2025-12-11 13:42:35',1),(2,'手机膜','2025-12-11 13:42:35',1),(3,'充电器','2025-12-11 13:42:35',1),(4,'耳机','2025-12-11 13:42:35',1),(5,'其他手机配件','2025-12-11 13:42:35',1),(6,'键盘','2025-12-11 13:42:35',2),(7,'鼠标','2025-12-11 13:42:35',2),(8,'耳机','2025-12-11 13:42:35',2),(9,'U盘','2025-12-11 13:42:35',2),(10,'移动硬盘','2025-12-11 13:42:35',2),(11,'其他电脑配件','2025-12-11 13:42:35',2),(12,'手机','2025-12-11 13:42:35',3),(13,'平板','2025-12-11 13:42:35',3),(14,'电脑','2025-12-11 13:42:35',3),(15,'相机','2025-12-11 13:42:35',3),(16,'其他数码设备','2025-12-11 13:42:35',3),(17,'文学类','2025-12-11 13:42:35',4),(18,'理学类','2025-12-11 13:42:35',4),(19,'工学类','2025-12-11 13:42:35',4),(20,'农学类','2025-12-11 13:42:35',4),(21,'医学类','2025-12-11 13:42:35',4),(22,'经济学','2025-12-11 13:42:35',4),(23,'管理学','2025-12-11 13:42:35',4),(24,'法学类','2025-12-11 13:42:35',4),(25,'教育学','2025-12-11 13:42:35',4),(26,'其他专业书籍','2025-12-11 13:42:35',4),(27,'专业课教材','2025-12-11 13:42:35',5),(28,'公共课教材','2025-12-11 13:42:35',5),(29,'考研资料','2025-12-11 13:42:35',5),(30,'考证资料','2025-12-11 13:42:35',5),(31,'其他教材教辅','2025-12-11 13:42:35',5),(32,'小说','2025-12-11 13:42:35',6),(33,'散文','2025-12-11 13:42:35',6),(34,'诗歌','2025-12-11 13:42:35',6),(35,'历史','2025-12-11 13:42:35',6),(36,'哲学','2025-12-11 13:42:35',6),(37,'其他课外读物','2025-12-11 13:42:35',6),(38,'笔类','2025-12-11 13:42:35',7),(39,'笔记本','2025-12-11 13:42:35',7),(40,'文件夹','2025-12-11 13:42:35',7),(41,'计算器','2025-12-11 13:42:35',7),(42,'其他文具','2025-12-11 13:42:35',7),(43,'收纳用品','2025-12-11 13:42:35',8),(44,'清洁用品','2025-12-11 13:42:35',8),(45,'家居装饰','2025-12-11 13:42:35',8),(46,'其他生活用品','2025-12-11 13:42:35',8),(47,'台灯','2025-12-11 13:42:35',9),(48,'风扇','2025-12-11 13:42:35',9),(49,'吹风机','2025-12-11 13:42:35',9),(50,'其他小家电','2025-12-11 13:42:35',9),(51,'护肤品','2025-12-11 13:42:35',10),(52,'化妆品','2025-12-11 13:42:35',10),(53,'个人护理','2025-12-11 13:42:35',10),(54,'其他美妆个护','2025-12-11 13:42:35',10),(55,'上衣','2025-12-11 13:42:35',11),(56,'裤子','2025-12-11 13:42:35',11),(57,'裙子','2025-12-11 13:42:35',11),(58,'外套','2025-12-11 13:42:35',11),(59,'其他服装','2025-12-11 13:42:35',11),(60,'鞋子','2025-12-11 13:42:35',12),(61,'背包','2025-12-11 13:42:35',12),(62,'钱包','2025-12-11 13:42:35',12),(63,'其他鞋包','2025-12-11 13:42:35',12),(64,'帽子','2025-12-11 13:42:35',13),(65,'围巾','2025-12-11 13:42:35',13),(66,'手套','2025-12-11 13:42:35',13),(67,'其他配饰','2025-12-11 13:42:35',13),(68,'球类','2025-12-11 13:42:35',14),(69,'健身器材','2025-12-11 13:42:35',14),(70,'瑜伽用品','2025-12-11 13:42:35',14),(71,'其他运动器材','2025-12-11 13:42:35',14),(72,'运动衣','2025-12-11 13:42:35',15),(73,'运动鞋','2025-12-11 13:42:35',15),(74,'运动配件','2025-12-11 13:42:35',15),(75,'其他运动服饰','2025-12-11 13:42:35',15),(76,'其他物品','2025-12-11 13:42:35',16);
/*!40000 ALTER TABLE `specifickinds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usercollection`
--

DROP TABLE IF EXISTS `usercollection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usercollection` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int DEFAULT NULL,
  `sid` int NOT NULL COMMENT '商品ID',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `admin_id` int DEFAULT NULL COMMENT '管理员ID',
  `user_type` tinyint(1) DEFAULT '0' COMMENT '用户类型(0普通用户,1管理员)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uid_sid` (`uid`,`sid`),
  KEY `sid` (`sid`),
  KEY `idx_collect_uid` (`uid`),
  KEY `idx_collect_admin_id` (`admin_id`),
  KEY `idx_collect_user_type` (`user_type`),
  CONSTRAINT `fk_collect_admin` FOREIGN KEY (`admin_id`) REFERENCES `admininformation` (`id`),
  CONSTRAINT `usercollection_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usercollection`
--

LOCK TABLES `usercollection` WRITE;
/*!40000 ALTER TABLE `usercollection` DISABLE KEYS */;
/*!40000 ALTER TABLE `usercollection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userinformation`
--

DROP TABLE IF EXISTS `userinformation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userinformation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `phone` varchar(20) NOT NULL COMMENT '手机号码',
  `realname` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `clazz` varchar(100) DEFAULT NULL COMMENT '班级',
  `sno` varchar(50) DEFAULT NULL COMMENT '学号',
  `dormitory` varchar(100) DEFAULT NULL COMMENT '宿舍',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别(0女,1男)',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像路径',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户基本信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userinformation`
--

LOCK TABLES `userinformation` WRITE;
/*!40000 ALTER TABLE `userinformation` DISABLE KEYS */;
INSERT INTO `userinformation` VALUES (1,'2025-12-12 13:18:36','test_user_foreign_key','13800138000',NULL,NULL,NULL,NULL,NULL,'2025-12-12 13:18:36',NULL,NULL),(2,'2025-12-12 22:25:54','testU1','13611614480','张平文',NULL,'2023302111000','文理学部B栋',NULL,'2025-12-12 20:28:43',NULL,NULL),(3,'2025-12-13 00:06:46','testU2','13611611111','小张',NULL,'20241234321','工学部3舍',0,'2025-12-13 00:05:14',NULL,'1234567@qq.com'),(4,'2025-12-13 00:15:19','随便起一个','13916722480','小李',NULL,'2023302111300','信息学部B栋',NULL,'2025-12-13 00:14:20',NULL,NULL),(5,'2025-12-13 00:59:29','求不挂科','13611614481','小文',NULL,'20211234567','文理学部C栋',0,'2025-12-13 00:58:15',NULL,'freeCursorTry2@163.com'),(6,'2025-12-13 01:11:12','注册测试','13916722487',NULL,NULL,NULL,NULL,NULL,'2025-12-13 01:11:12',NULL,NULL);
/*!40000 ALTER TABLE `userinformation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userpassword`
--

DROP TABLE IF EXISTS `userpassword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userpassword` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `uid` int NOT NULL COMMENT '用户ID',
  `password` varchar(100) NOT NULL COMMENT '密码(加密)',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `userpassword_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `userinformation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户密码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userpassword`
--

LOCK TABLES `userpassword` WRITE;
/*!40000 ALTER TABLE `userpassword` DISABLE KEYS */;
INSERT INTO `userpassword` VALUES (1,'2025-12-12 20:28:43',2,'e10adc3949ba59abbe56e057f20f883e'),(2,'2025-12-13 00:05:14',3,'f4d4cd1a39ce7baa803ef5a8ef4ad0db'),(3,'2025-12-13 00:14:20',4,'e10adc3949ba59abbe56e057f20f883e'),(4,'2025-12-13 00:58:15',5,'d5397f1497b5cdaad7253fdc92db610b'),(5,'2025-12-13 01:11:12',6,'a8698009bce6d1b8c2128eddefc25aad');
/*!40000 ALTER TABLE `userpassword` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userrelease`
--

DROP TABLE IF EXISTS `userrelease`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userrelease` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int NOT NULL COMMENT '用户ID',
  `sid` int NOT NULL COMMENT '商品ID',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `display` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  KEY `sid` (`sid`),
  CONSTRAINT `userrelease_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `userinformation` (`id`),
  CONSTRAINT `userrelease_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `shopinformation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户发布记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userrelease`
--

LOCK TABLES `userrelease` WRITE;
/*!40000 ALTER TABLE `userrelease` DISABLE KEYS */;
INSERT INTO `userrelease` VALUES (1,4,5,'2025-12-13 00:16:02',1,'2025-12-12 16:21:38'),(2,5,6,'2025-12-13 01:00:31',1,'2025-12-12 17:00:31');
/*!40000 ALTER TABLE `userrelease` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userstate`
--

DROP TABLE IF EXISTS `userstate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userstate` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int NOT NULL COMMENT '用户ID',
  `state` tinyint DEFAULT '0' COMMENT '用户状态(0正常,1禁用)',
  PRIMARY KEY (`id`),
  KEY `uid` (`uid`),
  CONSTRAINT `userstate_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `userinformation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userstate`
--

LOCK TABLES `userstate` WRITE;
/*!40000 ALTER TABLE `userstate` DISABLE KEYS */;
/*!40000 ALTER TABLE `userstate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userwant`
--

DROP TABLE IF EXISTS `userwant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userwant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `uid` int DEFAULT NULL COMMENT '普通用户ID',
  `name` varchar(255) NOT NULL COMMENT '求购物品名称',
  `price` decimal(10,2) NOT NULL COMMENT '期望价格',
  `context` text COMMENT '求购描述',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `display` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  `sort` int DEFAULT '0' COMMENT '排序',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `remark` text COMMENT '备注',
  `admin_id` int DEFAULT NULL COMMENT '管理员ID',
  `user_type` tinyint(1) DEFAULT '0' COMMENT '用户类型(0普通用户,1管理员)',
  PRIMARY KEY (`id`),
  KEY `idx_display` (`display`),
  KEY `idx_uid` (`uid`),
  KEY `idx_userwant_uid` (`uid`),
  KEY `idx_userwant_admin_id` (`admin_id`),
  KEY `idx_userwant_type` (`user_type`),
  CONSTRAINT `userwant_admin_fk` FOREIGN KEY (`admin_id`) REFERENCES `admininformation` (`id`),
  CONSTRAINT `userwant_uid_fk` FOREIGN KEY (`uid`) REFERENCES `userinformation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户求购表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userwant`
--

LOCK TABLES `userwant` WRITE;
/*!40000 ALTER TABLE `userwant` DISABLE KEYS */;
INSERT INTO `userwant` VALUES (3,1,'黑色手机壳',5.00,NULL,'2025-12-12 13:28:16','2025-12-12 14:03:44',0,1,1,'vx:123456',NULL,0),(4,1,'黑色手机壳',5.00,NULL,'2025-12-12 13:28:22','2025-12-12 13:28:28',0,1,1,'vx:123456',NULL,0),(5,1,'黑色手机壳',5.00,NULL,'2025-12-12 14:03:37','2025-12-12 14:42:03',0,1,1,'vx:123456',NULL,0),(6,1,'黑色手机壳',5.00,NULL,'2025-12-12 14:42:01','2025-12-12 14:53:09',0,1,1,'vx:123456',NULL,0),(7,1,'黑色手机壳',5.00,NULL,'2025-12-12 14:53:04','2025-12-12 14:53:20',0,1,1,'qq:111',NULL,0),(8,1,'黑色手机壳',5.00,NULL,'2025-12-12 14:53:18','2025-12-12 14:53:22',0,1,1,'qq:111',NULL,0),(9,1,'黑色手机壳',5.00,NULL,'2025-12-12 14:53:33','2025-12-12 14:53:33',1,1,1,'qq:1111',NULL,0),(10,5,'鼠标垫',10.00,NULL,'2025-12-13 01:02:02','2025-12-13 01:02:02',0,11,1,'QQ：随便写一个',NULL,0);
/*!40000 ALTER TABLE `userwant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userwant_fk_backup`
--

DROP TABLE IF EXISTS `userwant_fk_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userwant_fk_backup` (
  `id` int NOT NULL DEFAULT '0' COMMENT 'ID',
  `uid` int DEFAULT NULL COMMENT '普通用户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '求购物品名称',
  `price` decimal(10,2) NOT NULL COMMENT '期望价格',
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '求购描述',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `display` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示(1显示,0隐藏)',
  `sort` int DEFAULT '0' COMMENT '排序',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '备注',
  `admin_id` int DEFAULT NULL COMMENT '管理员ID',
  `user_type` tinyint(1) DEFAULT '0' COMMENT '用户类型(0普通用户,1管理员)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userwant_fk_backup`
--

LOCK TABLES `userwant_fk_backup` WRITE;
/*!40000 ALTER TABLE `userwant_fk_backup` DISABLE KEYS */;
/*!40000 ALTER TABLE `userwant_fk_backup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wantcontext`
--

DROP TABLE IF EXISTS `wantcontext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wantcontext` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `wid` int NOT NULL COMMENT '求购ID',
  `context` text NOT NULL COMMENT '求购详情内容',
  PRIMARY KEY (`id`),
  KEY `wid` (`wid`),
  CONSTRAINT `wantcontext_ibfk_1` FOREIGN KEY (`wid`) REFERENCES `userwant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='求购详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wantcontext`
--

LOCK TABLES `wantcontext` WRITE;
/*!40000 ALTER TABLE `wantcontext` DISABLE KEYS */;
/*!40000 ALTER TABLE `wantcontext` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-13  1:29:47
