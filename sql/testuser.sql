/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : testuser

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 18/11/2025 14:52:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `identity_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `identity_card`) VALUES (1, 'jack', '5Jep9EQw3PfXwX/0yQp2cw==:jYPWgB5R3pi5KG21FjHiaw==', 'yRKP6m6l0rZAlpdHoJjyJg==:pT1XpnZ17KmR3/Jd3Vr0Rw==', 'J32VkMaSZxTvi+r5604Oew==:SIAcUfAlIItgEZ0Ojks+9QIXBJvP1jGoWCpaTaypz0Y=', 'nNa9EsUd3onB9NuJDsmqJg==:5jUYGQoiAFWTETpRaLdtdGWaHupwBYr0iQhEPU6IWkI=');
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `identity_card`) VALUES (2, 'Alice', '5Jep9EQw3PfXwX/0yQp2cw==:jYPWgB5R3pi5KG21FjHiaw==', 'BQzRbXSTFb1XoBUGpY0YIA==:RECWUuY8QlZgyh7AOIUwXg==', 'izlmh4ZuRV2s260xZKObFA==:UJPMjRZgL/YwcsV2onvpNnakvdPQKIOWjKeIyWocn8s=', 'UlLremEKJ4hDR6k3tEgSiA==:ODRHfc8gm2JqekfGf7ep8sEVM2n9iz+YCRiB1+PoZdQ=');
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `identity_card`) VALUES (3, 'AESTest1', 'GLWGYFrU7+UTnhQhlXeWTQ==:eoJ5O0JRTLndCzYmo/GwhA==', 'wVElN4+j/KNewjcGPAURNg==:q34MotibtYIzo9oU2N5c8A==', 'jVraOZMkAc+Lhu7tSO1Wtw==:lNYyPAnY8OJspteVAuMEQk2KcmSXBqpcN4kQZMWb64g=', '1CLLEc2iHSqCZiYeyt6M5w==:svAguG4jSYSWyZW4t79agxpIAYTAj1N9UrKKGc1vrCo=');
INSERT INTO `user` (`id`, `username`, `password`, `phone`, `email`, `identity_card`) VALUES (4, 'test001', 'KDl24GYtRQqP7uSccO9/7w==:ovvafubkh8eSeynU2IUTbQ==', '60vu+iXUweXYetP2RZSS6Q==:Qdl/0n+9IzSfS5fhNoOAOw==', 'iwzrp2V8uU6dyXwdpJ8HJQ==:iNVQUyh+g8FlwC4Oak4zQ35I1PXzltjCBLny+zZNYD4=', 'OqhhW1p5UYx9bmEvhDtkDA==:BUkWkd+zUB+fhNaONBwJ+v/9T7LGPsLc548x8RXfU7E=');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
