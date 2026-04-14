USE vhr;
TRUNCATE TABLE menu;

INSERT INTO menu (id, name, url, path, component, parentId, enabled, iconCls) VALUES
(1, '系统管理', '/system/**', '/system', NULL, NULL, 1, 'fa fa-windows'),
(2, '人事管理', '/employee/**', '/emp', NULL, NULL, 1, 'fa fa-user-circle'),
(3, '工资管理', '/salary/**', '/sal', NULL, NULL, 1, 'fa fa-money'),
(4, '统计管理', '/statistics/**', '/sta', NULL, NULL, 1, 'fa fa-bar-chart'),
(5, '招聘管理', '/recruitment/**', '/recruitment', NULL, NULL, 1, 'fa fa-bullhorn'),
(6, '基础信息设置', '/system/basic/**', '/basic', 'SysBasic', 1, 1, NULL),
(7, '操作员管理', '/system/operator/**', '/operator', 'SysHr', 1, 1, NULL),
(8, '角色管理', '/system/role/**', '/role', 'SysRole', 1, 1, NULL),
(9, '权限组管理', '/system/perm/**', '/perm', 'SysPerm', 1, 1, NULL),
(10, '员工资料', '/employee/basic/**', '/empbasic', 'EmpBasic', 2, 1, NULL),
(11, '员工账套', '/employee/salary/**', '/empsalary', 'EmpSalary', 2, 1, NULL),
(12, '员工培训', '/employee/train/**', '/emptrain', 'EmpTrain', 2, 1, NULL),
(13, '员工奖惩', '/employee/ec/**', '/empec', 'EmpEc', 2, 1, NULL),
(14, '员工调动', '/employee/remove/**', '/empremove', 'EmpRemove', 2, 1, NULL),
(15, '高级检索', '/employee/advanced/**', '/empadv', 'EmpAdv', 2, 1, NULL),
(16, '工资账套管理', '/salary/sob/**', '/salsob', 'SalSob', 3, 1, NULL),
(17, '员工工资查询', '/salary/table/**', '/saltable', 'SalTable', 3, 1, NULL),
(18, '人事统计', '/statistics/personnel/**', '/stapersonnel', 'StaPersonnel', 4, 1, NULL),
(19, '员工记录', NULL, '/empRecord', 'PerRecord', 2, 1, NULL),
(20, '简历管理', NULL, '/resume', 'recruitment/ResumeMana', 5, 1, NULL),
(21, '面试安排', NULL, '/interview', 'recruitment/InterviewMana', 5, 1, NULL),
(22, 'Offer管理', NULL, '/offer', 'recruitment/OfferMana', 5, 1, NULL);

TRUNCATE TABLE menu_role;
INSERT INTO menu_role (mid, rid) VALUES
(1,1),(2,1),(3,1),(4,1),(5,1),
(6,1),(7,1),(8,1),(9,1),
(10,1),(11,1),(12,1),(13,1),(14,1),(15,1),(19,1),
(16,1),(17,1),
(18,1),
(20,1),(21,1),(22,1);

SELECT parentId, GROUP_CONCAT(name) as menus FROM menu GROUP BY parentId;
