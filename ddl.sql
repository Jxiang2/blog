/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS `ms_article`;
CREATE TABLE `ms_article` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `comment_counts` int(11) DEFAULT NULL,
                              `create_date` bigint(20) DEFAULT NULL,
                              `summary` varchar(255) DEFAULT NULL,
                              `title` varchar(64) DEFAULT NULL,
                              `view_counts` int(11) DEFAULT NULL,
                              `author_id` bigint(20) DEFAULT NULL,
                              `body_id` bigint(20) DEFAULT NULL,
                              `category_id` bigint(20) DEFAULT NULL,
                              `deleted` tinyint(1) NOT NULL DEFAULT '0',
                              PRIMARY KEY (`id`),
                              FULLTEXT KEY `idx_title_summary` (`title`,`summary`)
) ENGINE=InnoDB AUTO_INCREMENT=1588010514855600130 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ms_article_body`;
CREATE TABLE `ms_article_body` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `content` longtext,
                                   `content_html` longtext,
                                   `article_id` bigint(20) NOT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `article_id` (`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1588010515077898243 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ms_article_tag`;
CREATE TABLE `ms_article_tag` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `article_id` bigint(20) NOT NULL,
                                  `tag_id` bigint(20) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `article_id` (`article_id`),
                                  KEY `tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ms_category`;
CREATE TABLE `ms_category` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                               `deleted` tinyint(1) NOT NULL DEFAULT '0',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1588009326122631171 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ms_comment`;
CREATE TABLE `ms_comment` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              `create_date` bigint(20) NOT NULL,
                              `article_id` bigint(20) NOT NULL,
                              `author_id` bigint(20) NOT NULL,
                              `parent_id` bigint(20) DEFAULT NULL,
                              `to_uid` bigint(20) NOT NULL,
                              `level` int(11) NOT NULL,
                              `deleted` tinyint(1) NOT NULL DEFAULT '0',
                              PRIMARY KEY (`id`),
                              KEY `article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ms_sys_user`;
CREATE TABLE `ms_sys_user` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `account` varchar(64) DEFAULT NULL,
                               `admin` bit(1) DEFAULT NULL,
                               `create_date` bigint(20) DEFAULT NULL,
                               `deleted` bit(1) DEFAULT NULL,
                               `github` varchar(128) DEFAULT NULL,
                               `last_login` bigint(20) DEFAULT NULL,
                               `nickname` varchar(255) DEFAULT NULL,
                               `password` varchar(64) DEFAULT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1587990725382746115 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ms_tag`;
CREATE TABLE `ms_tag` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                          `deleted` tinyint(1) NOT NULL DEFAULT '0',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
