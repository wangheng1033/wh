-- 招聘管理模块数据库脚本
-- 包含简历表、面试表、offer表

-- ----------------------------
-- 简历表 (resume)
-- ----------------------------
DROP TABLE IF EXISTS `resume`;

CREATE TABLE `resume` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '简历ID',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `gender` char(4) DEFAULT NULL COMMENT '性别',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `education` varchar(16) DEFAULT NULL COMMENT '学历（博士/硕士/本科/大专/高中/其他）',
  `work_years` int(3) DEFAULT NULL COMMENT '工作年限',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `expected_salary` varchar(32) DEFAULT NULL COMMENT '期望薪资',
  `job_intention` varchar(64) DEFAULT NULL COMMENT '求职意向（岗位）',
  `work_experience` text COMMENT '工作经历',
  `education_experience` text COMMENT '教育经历',
  `status` tinyint(1) DEFAULT '0' COMMENT '简历状态：0-待筛选，1-已筛选，2-待面试，3-已拒绝，4-已录用',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '简历附件URL',
  `attachment_name` varchar(128) DEFAULT NULL COMMENT '简历附件名称',
  `source` tinyint(1) DEFAULT '0' COMMENT '简历来源：0-手动录入，1-Excel导入',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_education` (`education`),
  KEY `idx_job_intention` (`job_intention`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='简历表';

-- ----------------------------
-- 面试表 (interview)
-- ----------------------------
DROP TABLE IF EXISTS `interview`;

CREATE TABLE `interview` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '面试ID',
  `resume_id` int(11) NOT NULL COMMENT '简历ID',
  `candidate_name` varchar(32) DEFAULT NULL COMMENT '候选人姓名（冗余字段）',
  `position_id` int(11) DEFAULT NULL COMMENT '面试岗位ID',
  `position_name` varchar(64) DEFAULT NULL COMMENT '面试岗位名称',
  `interview_time` datetime DEFAULT NULL COMMENT '面试时间',
  `interview_location` varchar(128) DEFAULT NULL COMMENT '面试地点',
  `interviewer_id` int(11) DEFAULT NULL COMMENT '面试官ID（关联hr表）',
  `interviewer_name` varchar(32) DEFAULT NULL COMMENT '面试官姓名',
  `interview_type` tinyint(1) DEFAULT '1' COMMENT '面试类型：1-初面，2-复面，3-终面',
  `status` tinyint(1) DEFAULT '0' COMMENT '面试状态：0-待面试，1-已通过，2-未通过，3-待定',
  `score` int(3) DEFAULT NULL COMMENT '面试打分（0-100）',
  `advantages` text COMMENT '优点评价',
  `disadvantages` text COMMENT '缺点评价',
  `suggestion` tinyint(1) DEFAULT '0' COMMENT '录用建议：0-待定，1-建议录用，2-不建议录用',
  `evaluation` text COMMENT '综合评价',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '面试记录附件',
  `notify_status` tinyint(1) DEFAULT '0' COMMENT '通知状态：0-未通知，1-已通知面试官，2-已通知候选人',
  `notify_content` text COMMENT '通知内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_resume_id` (`resume_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_interviewer_id` (`interviewer_id`),
  KEY `idx_interview_time` (`interview_time`),
  KEY `idx_status` (`status`),
  CONSTRAINT `interview_ibfk_1` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE,
  CONSTRAINT `interview_ibfk_2` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `interview_ibfk_3` FOREIGN KEY (`interviewer_id`) REFERENCES `hr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='面试表';

-- ----------------------------
-- Offer表 (offer)
-- ----------------------------
DROP TABLE IF EXISTS `offer`;

CREATE TABLE `offer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Offer ID',
  `resume_id` int(11) NOT NULL COMMENT '简历ID',
  `interview_id` int(11) DEFAULT NULL COMMENT '面试ID',
  `candidate_name` varchar(32) DEFAULT NULL COMMENT '候选人姓名',
  `position_id` int(11) DEFAULT NULL COMMENT '岗位ID',
  `position_name` varchar(64) DEFAULT NULL COMMENT '岗位名称',
  `salary` varchar(32) DEFAULT NULL COMMENT '薪资',
  `probation_period` int(2) DEFAULT '3' COMMENT '试用期（月）',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `offer_valid_date` date DEFAULT NULL COMMENT 'Offer有效期',
  `status` tinyint(1) DEFAULT '0' COMMENT 'Offer状态：0-待确认，1-已发送，2-已接受，3-已拒绝，4-已过期',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `view_status` tinyint(1) DEFAULT '0' COMMENT '查看状态：0-未查看，1-已查看',
  `view_time` datetime DEFAULT NULL COMMENT '查看时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `notify_hr_status` tinyint(1) DEFAULT '0' COMMENT 'HR提醒状态：0-未提醒，1-已提醒',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_resume_id` (`resume_id`),
  KEY `idx_interview_id` (`interview_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_status` (`status`),
  KEY `idx_entry_date` (`entry_date`),
  CONSTRAINT `offer_ibfk_1` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE,
  CONSTRAINT `offer_ibfk_2` FOREIGN KEY (`interview_id`) REFERENCES `interview` (`id`) ON DELETE SET NULL,
  CONSTRAINT `offer_ibfk_3` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `offer_ibfk_4` FOREIGN KEY (`create_by`) REFERENCES `hr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Offer表';

-- ----------------------------
-- 添加菜单数据
-- ----------------------------

-- 招聘管理一级菜单
INSERT INTO `menu` (`id`, `url`, `path`, `component`, `name`, `iconCls`, `keepAlive`, `requireAuth`, `parentId`, `enabled`) 
VALUES (100, '/', NULL, NULL, '招聘管理', 'el-icon-s-custom', NULL, 1, NULL, 1);

-- 简历管理子菜单
INSERT INTO `menu` (`id`, `url`, `path`, `component`, `name`, `iconCls`, `keepAlive`, `requireAuth`, `parentId`, `enabled`) 
VALUES (101, '/recruit/resume', 'RecruitResume', 'RecruitResume', '简历管理', NULL, NULL, 1, 100, 1);

-- 面试安排子菜单
INSERT INTO `menu` (`id`, `url`, `path`, `component`, `name`, `iconCls`, `keepAlive`, `requireAuth`, `parentId`, `enabled`) 
VALUES (102, '/recruit/interview', 'RecruitInterview', 'RecruitInterview', '面试安排', NULL, NULL, 1, 100, 1);

-- Offer管理子菜单
INSERT INTO `menu` (`id`, `url`, `path`, `component`, `name`, `iconCls`, `keepAlive`, `requireAuth`, `parentId`, `enabled`) 
VALUES (103, '/recruit/offer', 'RecruitOffer', 'RecruitOffer', 'Offer管理', NULL, NULL, 1, 100, 1);

-- ----------------------------
-- 添加权限数据（仅HR和管理员可操作）
-- ----------------------------

-- 招聘管理权限
INSERT INTO `menu_role` (`mid`, `rid`) VALUES (100, 1); -- 管理员
INSERT INTO `menu_role` (`mid`, `rid`) VALUES (100, 3); -- HR

INSERT INTO `menu_role` (`mid`, `rid`) VALUES (101, 1);
INSERT INTO `menu_role` (`mid`, `rid`) VALUES (101, 3);

INSERT INTO `menu_role` (`mid`, `rid`) VALUES (102, 1);
INSERT INTO `menu_role` (`mid`, `rid`) VALUES (102, 3);

INSERT INTO `menu_role` (`mid`, `rid`) VALUES (103, 1);
INSERT INTO `menu_role` (`mid`, `rid`) VALUES (103, 3);
