-- ----------------------------
--  Table structure for `sli_page_info`
-- ----------------------------
CREATE TABLE `sli_page_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `url` varchar(512) NOT NULL COMMENT 'url of a page',
  `page_type` tinyint(4) DEFAULT NULL COMMENT 'page type.Homepage:0,List:1,Detail:2',
  `page_content` longtext COMMENT 'html content',
  `checksum` varchar(128) DEFAULT NULL COMMENT 'checksum for detecting repeated page',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `features` varchar(256) DEFAULT NULL COMMENT 'reserved column',
  `site_id` mediumint(9) NOT NULL COMMENT 'site id',
  `minified_content` longtext COMMENT 'minified content',
  `cat_id` int(11) DEFAULT NULL COMMENT 'category id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `url` (`url`)
);
