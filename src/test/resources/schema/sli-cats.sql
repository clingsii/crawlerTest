-- ----------------------------
--  Table structure for `sli_cats`
-- ----------------------------
CREATE TABLE `sli_cats` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `cat_id` int(11) NOT NULL COMMENT 'category id',
  `cat_name` varchar(255) DEFAULT NULL COMMENT 'category name',
  `parent_id` int(11) DEFAULT NULL COMMENT 'parent category id',
  `site_id` int(11) NOT NULL COMMENT 'site id',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cat_id` (`cat_id`)
);
