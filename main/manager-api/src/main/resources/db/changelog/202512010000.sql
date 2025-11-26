-- 记忆体信息
CREATE TABLE IF NOT EXISTS `ai_memory` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '所属用户ID',
  `name` varchar(100) DEFAULT NULL COMMENT '记忆体名称',
  `mem_model_id` varchar(32) DEFAULT NULL COMMENT '记忆模型标识',
  `summary_memory` text COMMENT '总结记忆',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记忆体信息';

-- 设备表增加记忆体ID
ALTER TABLE `ai_device` ADD COLUMN `memory_id` varchar(32) DEFAULT NULL COMMENT '记忆体ID';
