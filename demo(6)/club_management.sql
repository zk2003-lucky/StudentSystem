/*
 Navicat Premium Data Transfer

 Source Server         : 111
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : club_management

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 20/04/2025 11:38:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for club
-- ----------------------------
DROP TABLE IF EXISTS `club`;
CREATE TABLE `club`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of club
-- ----------------------------
INSERT INTO `club` VALUES (1, '蔡徐坤', '1.jpg', '蔡徐坤的真爱粉');
INSERT INTO `club` VALUES (2, '篮球社', '10.jpg', '热爱篮球运动的社团，定期组织训练和比赛。');
INSERT INTO `club` VALUES (3, '足球社', '5.jpg', '足球爱好者的聚集地，欢迎加入我们的绿茵场！');
INSERT INTO `club` VALUES (4, '羽毛球社', '4.jpg', '轻羽飞扬，享受挥拍的乐趣。');
INSERT INTO `club` VALUES (5, '火影忍者社团', '9.png', '你看看你的后面呢？\n你再看看你的后面呢？\n你再再看看你的后面呢？');
INSERT INTO `club` VALUES (6, '我爱读书', '10.jpg', '以书会友，共同探索知识的海洋。');

-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `clubId` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `clubId`(`clubId` ASC) USING BTREE,
  CONSTRAINT `member_ibfk_1` FOREIGN KEY (`clubId`) REFERENCES `club` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (1, '蔡徐坤', 1);
INSERT INTO `member` VALUES (2, '张三', 3);
INSERT INTO `member` VALUES (3, '李四', 1);
INSERT INTO `member` VALUES (4, '王五', 1);
INSERT INTO `member` VALUES (5, '赵六', 1);
INSERT INTO `member` VALUES (6, '陈七', 1);
INSERT INTO `member` VALUES (7, '周杰', 2);
INSERT INTO `member` VALUES (8, '吴磊', 2);
INSERT INTO `member` VALUES (9, '郑爽', 2);
INSERT INTO `member` VALUES (10, '孙杨', 2);
INSERT INTO `member` VALUES (11, '刘菲', 2);
INSERT INTO `member` VALUES (12, '林丹', 3);
INSERT INTO `member` VALUES (13, '谢杏芳', 3);
INSERT INTO `member` VALUES (14, '鲍春来', 3);
INSERT INTO `member` VALUES (15, '谌龙', 3);
INSERT INTO `member` VALUES (16, '张宁', 3);
INSERT INTO `member` VALUES (17, '陈漫', 4);
INSERT INTO `member` VALUES (18, '薇薇安', 4);
INSERT INTO `member` VALUES (19, '布列松', 4);
INSERT INTO `member` VALUES (20, '安妮', 4);
INSERT INTO `member` VALUES (21, '森山大道', 4);
INSERT INTO `member` VALUES (22, '鲁迅', 5);
INSERT INTO `member` VALUES (23, '冰心', 5);
INSERT INTO `member` VALUES (24, '老舍', 5);
INSERT INTO `member` VALUES (25, '巴金', 5);
INSERT INTO `member` VALUES (26, '莫言', 5);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` int NULL DEFAULT NULL,
  `role` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1, 'ADMIN');
INSERT INTO `user_role` VALUES (2, 1, 'USER');
INSERT INTO `user_role` VALUES (3, 2, 'USER');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'lmy', '$2a$10$j5qD5j8FZkhdhmvhPGuieeAudJ80I/AsETBxW9eBw89goO1Spgpy2');
INSERT INTO `users` VALUES (2, 'chen', '$2a$10$j5qD5j8FZkhdhmvhPGuieeAudJ80I/AsETBxW9eBw89goO1Spgpy2');

SET FOREIGN_KEY_CHECKS = 1;
