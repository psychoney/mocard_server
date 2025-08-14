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

 Date: 16/05/2023 15:24:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for card_info
-- ----------------------------
DROP TABLE IF EXISTS `card_info`;
CREATE TABLE `card_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `title` varchar(1000) DEFAULT NULL COMMENT '卡片名称',
  `description` varchar(2000) DEFAULT NULL COMMENT '卡片描述',
  `imageUrl` varchar(255) DEFAULT NULL COMMENT '卡片图片',
  `type` varchar(255) DEFAULT NULL COMMENT '卡片类型',
  `color` varchar(255) DEFAULT NULL COMMENT '卡片颜色',
  `fewshot` int(11) DEFAULT '0' COMMENT 'few shot开关，0:关闭；1:开启',
  `createTime` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COMMENT='卡片信息';

-- ----------------------------
-- Records of card_info
-- ----------------------------
BEGIN;
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (1, '诗歌写作', '我是您的专属诗人，可以给您写诗', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'red', 0, '2023-05-10 14:59:34.000000');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (2, '心灵导师', '我是您的心灵导师，用名言给您心灵指引', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/003.png', 'LIFE', 'blue', 1, '2023-05-10 14:59:37.000000');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (3, '文案写作', '我将根据您提供文案主题，帮你撰写完整内容', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'green', 0, '2023-05-15 08:55:26.149752');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (4, '文章总结', '我将根据您输入的文章提炼要点', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'brown', 0, '2023-05-15 08:58:21.492467');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (5, '文字校对', '我将根据您输入的内容校对修改文字', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'grey', 0, '2023-05-15 09:00:40.664001');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (6, '故事创作', '我将根据您输入的主题创作故事', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'yellow', 0, '2023-05-15 09:01:24.558402');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (7, '续写文字', '我将根据您输入的内容进行扩充续写', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'pink', 0, '2023-05-15 09:01:56.683957');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (8, '英文写作', '我将根据您输入的中文或英文润色为优美的英文', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'black', 0, '2023-05-15 09:05:38.385733');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (9, '邮件写作', '我将根据您输入的主题生成邮件内容', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'purple', 1, '2023-05-15 09:10:34.364425');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (10, '论文报告', '我将根据您输入的主题生成论文报告', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WRITING', 'teal', 0, '2023-05-15 09:10:54.997837');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (11, '周报写作', '我将根据您输入的工作内容生成完整周报', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'red', 0, '2023-05-15 09:12:25.393954');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (12, '中文翻译', '我将根据您输入的内容翻译为中文', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'blue', 0, '2023-05-15 09:44:02.367215');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (13, '英文翻译', '我将根据您输入的内容翻译为英文', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'green', 0, '2023-05-15 09:44:21.160779');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (14, '公关稿件', '我将根据您输入的主题撰写公关稿件', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'brown', 0, '2023-05-15 09:49:56.832616');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (15, '标题生成', '我将根据您输入的内容生成具有吸引力的标题', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'grey', 0, '2023-05-15 09:50:01.283445');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (16, '广告策划', '我将根据您输入的主题撰写广告脚本', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'yellow', 0, '2023-05-15 09:50:02.426396');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (17, '语法助手', '我是您的语法检查助手，我会修正您发来的英文语句中的拼写错误', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LEARNING', 'pink', 1, '2023-05-15 09:50:04.010034');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (18, 'IT专家', '我是您的IT助手，我会解决您的IT问题', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LEARNING', 'black', 0, '2023-05-15 09:50:06.527032');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (19, '节日祝福', '我将根据您输入的主题撰写节日祝福', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'purple', 1, '2023-05-15 09:50:08.625458');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (20, '情书情话', '我将根据您输入的主题撰写情书', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'red', 0, '2023-05-15 10:13:13.504574');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (21, '旅行向导', '我是您的旅行向导，给您提供旅行建议', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'teal', 0, '2023-05-15 10:13:57.972495');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (22, '心理医生', '我是您的心理医生，给您提供提供专业、有针对性的建议', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'blue', 0, '2023-05-15 10:15:50.596443');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (23, '职业顾问', '我是您的职业顾问，帮助您的技能、兴趣和经验确定最适合的职业', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'green', 0, '2023-05-15 10:17:54.436707');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (24, '创业助手', '我是您的创业顾问，根据您的意愿推荐创业点子', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'WORKING', 'brown', 0, '2023-05-15 10:19:31.246088');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (25, '宝宝取名', '我是您的取名顾问，帮您取一个好听的名字', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'grey', 0, '2023-05-15 10:33:49.019819');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (26, '知乎风格', '我将根据您输入的主题生成知乎风格发布内容', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'yellow', 0, '2023-05-15 10:33:50.843062');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (27, '小红书风格', '我将根据您输入的主题生成小红书风格发布内容', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'pink', 1, '2023-05-15 10:33:52.238458');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (28, '商品好评', '我将根据您输入的内容生成外卖、餐厅、商品好评', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'purple', 1, '2023-05-15 11:12:03.079472');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (29, '梦境解析', '我将根据您的描述解析梦境', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'black', 1, '2023-05-15 11:12:04.480095');
INSERT INTO `card_info` (`id`, `title`, `description`, `imageUrl`, `type`, `color`, `fewshot`, `createTime`) VALUES (30, '夸夸助手', '我将根据你想夸的人和点，帮您夸夸', 'https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png', 'LIFE', 'red', 1, '2023-05-15 11:12:05.515861');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
