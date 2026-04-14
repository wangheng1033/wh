USE vhr;

CREATE TABLE IF NOT EXISTS menu_role (
  id int(11) NOT NULL AUTO_INCREMENT,
  mid int(11) DEFAULT NULL,
  rid int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELETE FROM menu_role;

INSERT INTO menu_role(mid, rid) VALUES 
(1, 1), (2, 1), (3, 1), (4, 1),
(5, 1), (6, 1), (7, 1), (8, 1),
(9, 1), (10, 1), (11, 1), (12, 1),
(13, 1), (14, 1), (15, 1), (16, 1),
(17, 1), (18, 1), (19, 1), (20, 1),
(21, 1), (22, 1), (23, 1), (24, 1),
(25, 1), (26, 1), (27, 1), (28, 1),
(29, 1);

INSERT IGNORE INTO menu(id, name, path, component, iconCls, parentId, enabled) VALUES
(100, '招聘管理', '/recruitment', NULL, 'fa fa-bullhorn', NULL, 1),
(101, '简历管理', '/resume', 'recruitment/ResumeMana', NULL, 100, 1),
(102, '面试安排', '/interview', 'recruitment/InterviewMana', NULL, 100, 1),
(103, 'Offer管理', '/offer', 'recruitment/OfferMana', NULL, 100, 1);

INSERT INTO menu_role(mid, rid) VALUES (100, 1), (101, 1), (102, 1), (103, 1);

SELECT m.id, m.name, m.parentId, mr.rid as role_id
FROM menu m LEFT JOIN menu_role mr ON m.id = mr.mid
WHERE m.name LIKE '%招聘%' OR m.name LIKE '%简历%' OR m.name LIKE '%面试%' OR m.name LIKE '%Offer%'
ORDER BY m.id;
