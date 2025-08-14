/*
 Navicat Premium Data Transfer

 Source Server         : PLANeT PRD
 Source Server Type    : MySQL
 Source Server Version : 50724 (5.7.24)
 Source Host           : 120.48.73.246:3306
 Source Schema         : mocard

 Target Server Type    : MySQL
 Target Server Version : 50724 (5.7.24)
 File Encoding         : 65001

 Date: 16/05/2023 15:25:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `deviceId` varchar(255) DEFAULT NULL COMMENT '设备id',
  `email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
  `memberFlag` varchar(255) DEFAULT NULL COMMENT '会员标志',
  `status` varchar(255) DEFAULT NULL COMMENT '用户状态',
  `lastLogin` datetime DEFAULT NULL COMMENT '最后登录时间',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of user_account
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
