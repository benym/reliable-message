

--
-- Table structure for table `message_entity`
--

DROP TABLE IF EXISTS `message_entity`;

CREATE TABLE `message_entity` (
  `id` bigint NOT NULL COMMENT '主键',
  `notice_type` tinyint NOT NULL COMMENT '通知类型',
  `notice_content` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息实体表';

--
-- Dumping data for table `message_entity`
--

LOCK TABLES `message_entity` WRITE;
INSERT INTO `message_entity` (`id`, `notice_type`, `notice_content`, `create_time`, `update_time`) VALUES (1,0,'测试一下全局消息','2022-02-18 09:33:20','2022-02-18 09:33:20'),(2,1,'测试一下另外的消息','2022-02-18 09:33:20','2022-02-18 09:33:20');

UNLOCK TABLES;

--
-- Table structure for table `message_log`
--

DROP TABLE IF EXISTS `message_log`;

CREATE TABLE `message_log` (
  `id` bigint NOT NULL,
  `notice_user_id` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知id=notice_id+user_id',
  `notice_type` tinyint NOT NULL COMMENT '通知类型',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '事件同步状态，0表示prepared，1表示commit',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志位，1已删除，0未删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `updated_by` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `message_action` tinyint NOT NULL DEFAULT '0' COMMENT '消息动作类型，0表示新增，1表示更新，2表示删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `message_log_notice_id_uindex` (`notice_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--
-- Dumping data for table `message_log`
--

LOCK TABLES `message_log` WRITE;

INSERT INTO `message_log` (`id`, `notice_user_id`, `notice_type`, `status`, `deleted`, `create_time`, `update_time`, `created_by`, `updated_by`, `message_action`) VALUES (1495366070426750978,'11495366070393196546',0,1,0,'2022-02-20 11:53:48','2022-02-20 11:53:48','测试','测试',0),(1495366070435139585,'11495366070393196547',0,1,0,'2022-02-20 11:53:48','2022-02-20 11:53:48','测试','测试',0),(1496073025797554177,'11496072940602851329',0,1,0,'2022-02-22 10:43:00','2022-02-22 10:43:00','测试','测试',0),(1496073025814331393,'11496072940611239937',0,1,0,'2022-02-22 10:43:00','2022-02-22 10:43:00','测试','测试',0),(1496073497572839426,'11496073490614489090',0,0,0,'2022-02-22 10:44:52','2022-02-22 10:44:52','测试','测试',0),(1496073497581228034,'11496073490614489091',0,1,0,'2022-02-22 10:44:52','2022-02-22 10:44:52','测试','测试',0),(1496451399015526402,'11496451370670419969',0,1,0,'2022-02-23 11:46:31','2022-02-23 11:46:31','测试','测试',0),(1496451399032303617,'11496451370670419970',0,1,0,'2022-02-23 11:46:31','2022-02-23 11:46:31','测试','测试',0);

UNLOCK TABLES;

--
-- Table structure for table `message_success`
--

DROP TABLE IF EXISTS `message_success`;

CREATE TABLE `message_success` (
  `id` bigint NOT NULL COMMENT '主键',
  `unique_id` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一id，唯一索引幂等消息\nnotice_id+user_id+base64消息内容',
  `notice_id` bigint NOT NULL COMMENT '通知id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `message_success_unique_id_uindex` (`unique_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息成功记录表';


--
-- Dumping data for table `message_success`
--

LOCK TABLES `message_success` WRITE;

INSERT INTO `message_success` (`id`, `unique_id`, `notice_id`, `user_id`, `user_name`, `create_time`, `update_time`) VALUES (1495366072108666882,'114953660703931965465rWL6K+V5LiA5LiL5YWo5bGA5raI5oGv',1,1495366070393196546,'朋友0','2022-02-20 11:53:48','2022-02-20 11:53:48'),(1495366072112861186,'114953660703931965475rWL6K+V5LiA5LiL5YWo5bGA5raI5oGv',1,1495366070393196547,'朋友1','2022-02-20 11:53:48','2022-02-20 11:53:48'),(1496073043027755010,'114960729406028513295rWL6K+V5LiA5LiL5YWo5bGA5raI5oGv',1,1496072940602851329,'朋友0','2022-02-22 10:43:04','2022-02-22 10:43:04'),(1496073043031949314,'114960729406112399375rWL6K+V5LiA5LiL5YWo5bGA5raI5oGv',1,1496072940611239937,'朋友1','2022-02-22 10:43:04','2022-02-22 10:43:04'),(1496451410134626306,'114964513706704199695rWL6K+V5LiA5LiL5YWo5bGA5raI5oGv',1,1496451370670419969,'朋友0','2022-02-23 11:46:33','2022-02-23 11:46:33'),(1496451410143014914,'114964513706704199705rWL6K+V5LiA5LiL5YWo5bGA5raI5oGv',1,1496451370670419970,'朋友1','2022-02-23 11:46:33','2022-02-23 11:46:33');

UNLOCK TABLES;


-- Dump completed on 2022-04-09 16:55:20
